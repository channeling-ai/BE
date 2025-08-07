package channeling.be.domain.report.application;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.comment.domain.repository.CommentRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.report.presentation.ReportConverter;
import channeling.be.domain.report.presentation.ReportResDto;
import channeling.be.domain.task.domain.Task;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.global.infrastructure.jwt.JwtUtil;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.ReportHandler;
import channeling.be.response.exception.handler.TaskHandler;
import channeling.be.response.exception.handler.VideoHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.domain.VideoCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final TaskRepository taskRepository;
	private final ReportRepository reportRepository;
	private final CommentRepository commentRepository;

    private final VideoRepository videoRepository;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public ReportResDto.getReportAnalysisStatus getReportAnalysisStatus(Member member, Long taskId) {
        //task 조회 -> 없으면 애러 반환 -> 연관된 리포트 연관 조인
        Task task =taskRepository.findByIdFetchWithReportAndMember(taskId)
                .orElseThrow(()-> new TaskHandler(ErrorStatus._TASK_NOT_FOUND));
        // 만약 연관된 리포트가 없다면..? 오류 처리
        Report report = task.getReport();
        if (report == null) {
            throw new TaskHandler(ErrorStatus._TASK_NOT_REPORT);
        }
        // 만약 해당 리포트의 주인이 멤버가 아닌 경우 오류 처리
        if (!report.getVideo().getChannel().getMember().getId().equals(member.getId())) {
            throw new TaskHandler(ErrorStatus._REPORT_NOT_MEMBER);
        }
        return ReportConverter.toReportAnalysisStatus(task,report);
    }

	@Override
	public Report getReportByIdAndMember(Long reportId, Member member) {
		Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_FOUND));
		return reportRepository.findByReportAndMember(report.getId(), member.getId()).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_MEMBER));

	}

	@Override
	public ReportResDto.getCommentsByType getCommentsByType(Report report, CommentType commentType) {
		return ReportConverter.toCommentsByType(commentType, commentRepository.findTop5ByReportAndCommentType(report, commentType));
	}
	@Override
	public Page<ReportResDTO.ReportBrief> getChannelReportListByType(Long channelId, VideoCategory type, int page,
		int size) {
		Pageable pageable= PageRequest.of(page-1,size);
		Page<Report> reports=reportRepository.findByVideoChannelIdAndVideoVideoCategoryOrderByUpdatedAtDesc(channelId,type,pageable);

		return reports.map(ReportResDTO.ReportBrief::from);

	}

    @Override
    @Transactional
    public ReportResDto.createReport createReport(Member member, Long videoId) {
        // 요청 영상의 주인인지 확인
        if (!videoRepository.existsById(videoId)) {
            throw new VideoHandler(ErrorStatus._VIDEO_NOT_FOUND) ;
        }

        Video video = videoRepository.findByIdWithMemberId(videoId, member.getId())
                .orElseThrow(() -> new VideoHandler(ErrorStatus._VIDEO_NOT_MEMBER));


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
        Long taskId= sendPostToFastAPI(videoId, googleAccessToken);
        return new ReportResDto.createReport(taskId);
    }

    private static Long sendPostToFastAPI(Long videoId, String googleAccessToken) {

        // HTTP 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        // url 설정
        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8000/reports")
                .queryParam("video_id", videoId)
                .toUriString();

        // requestbody 생성
        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("googleAccessToken", googleAccessToken);

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
                throw new IllegalStateException("응답에 result.task_id가 없습니다.");
            }


        } catch (Exception e) {
            throw new RuntimeException("FastAPI 응답 파싱 실패", e);
        }    }
}
