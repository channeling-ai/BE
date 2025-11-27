package channeling.be.domain.report.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.comment.domain.repository.CommentRepository;
import channeling.be.domain.log.domain.DeleteType;
import channeling.be.domain.log.repository.ReportLogRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.domain.report.domain.PageType;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.report.presentation.ReportConverter;
import channeling.be.domain.report.presentation.ReportResDto;
import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.task.domain.Task;
import channeling.be.domain.task.domain.TaskStatus;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.domain.VideoType;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.ChannelHandler;
import channeling.be.response.exception.handler.ReportHandler;
import channeling.be.response.exception.handler.TaskHandler;
import channeling.be.response.exception.handler.VideoHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Primary
public class ReportServiceImpl implements ReportService {
    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final RedisUtil redisUtil;
    private final ReportDeleteService reportDeleteService;
    private final ChannelRepository channelRepository;
    private final ReportLogRepository reportLogRepository;

    //환경변수에서 FASTAPI_URL 환경변수 불러오기
    @Value("${FASTAPI_URL:http://localhost:8000}")
    private String baseFastApiUrl;

    @Override
    @Transactional(readOnly = true)
    public ReportResDto.getReportAnalysisStatus getReportAnalysisStatus(Member member, Long reportId) {
        // 리포트 존재 확인
        if (!reportRepository.existsById(reportId)) {
            throw new ReportHandler(ErrorStatus._REPORT_NOT_FOUND);
        }

        //리포트 주인 확인
        Report report = reportRepository.findByReportAndMember(reportId, member.getId()).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_MEMBER));

        //리포트로 연관된 task 조회 -> 없으면 애러 반환
        Task task =taskRepository.findByReportId(reportId)
                .orElseThrow(()-> new TaskHandler(ErrorStatus._TASK_NOT_FOUND));

        return ReportConverter.toReportAnalysisStatus(task,report);
    }

    @Override
    @Transactional(readOnly = true)
    public Report getReportByIdAndMember(Long reportId, Member member) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_FOUND));
        return reportRepository.findByReportAndMember(report.getId(), member.getId()).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_MEMBER));

    }

    @Override
    @Transactional(readOnly = true)
    public ReportResDto.getCommentsByType getCommentsByType(Report report, CommentType commentType) {
        return ReportConverter.toCommentsByType(commentType, commentRepository.findTop5ByReportAndCommentType(report, commentType));
    }

    @Override
    public Report checkReport(Long reportId, PageType type, Member member) {
        // TODO 태스크 삭제하지 않는다고 가정
        Task task = taskRepository.findByReportId(reportId)
                .orElseThrow(() -> new TaskHandler(ErrorStatus._REPORT_NOT_FOUND));

        switch (type) {
            case OVERVIEW:
                if (task.getOverviewStatus() != TaskStatus.COMPLETED)
                    throw new TaskHandler(ErrorStatus._REPORT_NOT_OVERVIEW);
                break;
            case ANALYSIS:
                if (task.getAnalysisStatus() != TaskStatus.COMPLETED)
                    throw new TaskHandler(ErrorStatus._REPORT_NOT_ANALYTICS);
                break;
            default:
                throw new IllegalArgumentException("Invalid PageType: " + type);
        }

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_FOUND));

        return reportRepository.findByReportAndMember(report.getId(), member.getId()).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_MEMBER));
    }
    @Override
    @Transactional
    public ReportResDto.deleteReport deleteReport(Member member, Long reportId) {
        reportRepository.findByReportAndMember(reportId, member.getId())
                .ifPresent(r -> {
                    reportDeleteService.deleteExistingReport(r, r.getVideo(), member, DeleteType.USER);
                });

        return new ReportResDto.deleteReport(reportId);
    }

    @Override
    @Transactional(readOnly = true)
	public Page<ReportResDTO.ReportBrief> getChannelReportListByType(Long channelId, VideoType type, int page,
		int size) {
		Pageable pageable= PageRequest.of(page-1,size);
		Page<Report> reports;
		if(VideoType.SHORTS.equals(type))
			reports=reportRepository.findByVideoChannelIdAndVideoVideoCategoryOrderByUpdatedAtDesc(channelId,VideoCategory.SHORTS,pageable);
		else
			reports=reportRepository.findByVideoChannelIdAndVideoVideoCategoryNotOrderByUpdatedAtDesc(channelId,VideoCategory.SHORTS,pageable);

        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelHandler(ErrorStatus._CHANNEL_NOT_FOUND));

        return reports.map(report -> ReportResDTO.ReportBrief.from(report, channel));

    }

    @Override
    public ReportResDto.createReport createReport(Member member, Long videoId) {
        // 이번달 멤버가 분석한 리포트 개수 조회
        if (!member.getPlan().equals(SubscriptionPlan.ADMIN)) {
            long currentCount = this.countMonthlyReports(member);
            member.checkReportCredit(currentCount);
        }

        // 요청 영상 존재 여부 확인
        if (!videoRepository.existsById(videoId)) {
            throw new VideoHandler(ErrorStatus._VIDEO_NOT_FOUND) ;
        }
        // 요청 영상의 주인인지 확인
        Video video = videoRepository.findByIdWithMemberId(videoId, member.getId())
                .orElseThrow(() -> new VideoHandler(ErrorStatus._VIDEO_NOT_MEMBER));

        // 멤버와 영상으로 기존에 분석했던 리포트가 존재하는 지 조회
        Optional<Report> optionalReport  = reportRepository.findByVideoAndMember(video.getId(), member.getId());

        //존재한다면 삭제
        optionalReport.ifPresent(report -> reportDeleteService.deleteExistingReport(report, video, member, DeleteType.AUTO));

        // redis에서 구글 토큰 가져오기 -> 2분 보다 적으면 에러 반환
        Long ttl = redisUtil.getGoogleAccessTokenExpire(member.getId());
        log.info("리포트 분석 전, 구글 토큰 TTL (초): {}" , ttl);
        if (ttl <= 120) {
            throw new ReportHandler(ErrorStatus._TOKEN_EXPIRED);
        }
        String googleAccessToken = redisUtil.getGoogleAccessToken(member.getId());
        log.info("구글 토큰 : {}" , googleAccessToken);
        // fastapi 쪽에 요청 보내기
        // 요청 바디에 보낼 객체 구성
        Long taskId = sendPostToFastAPI(videoId, googleAccessToken);
        // taskId로 리포트 조회
        Report report = reportRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_CREATE));

        return new ReportResDto.createReport(report.getId(), video.getId());
    }

    private Long sendPostToFastAPI(Long videoId, String googleAccessToken) {
//
        // HTTP 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        // url 설정
        String url = UriComponentsBuilder
                .fromHttpUrl(baseFastApiUrl+"/reports/v2")
                .queryParam("video_id", videoId)
                .toUriString();

        // requestbody 생성
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("googleAccessToken", googleAccessToken);

        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 생성
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // 응답 파싱 및 반환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode resultNode = root.get("result");
            if (resultNode != null && resultNode.has("task_id")) {
                return resultNode.get("task_id").asLong();
            } else {
                throw new IllegalStateException("응답에 id가 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 응답 파싱 실패", e);
        }
    }

    private Long countMonthlyReports(Member member) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long logCount = reportLogRepository.countMonthlyReports(member.getId(), startOfMonth);
        long reportCount = reportRepository.countMonthlyReports(member.getId(), startOfMonth);
        return logCount + reportCount;
    }

}
