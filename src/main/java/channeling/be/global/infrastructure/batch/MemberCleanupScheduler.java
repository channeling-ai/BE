package channeling.be.global.infrastructure.batch;

import channeling.be.domain.TrendKeyword.domain.TrendKeywordRepository;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.comment.domain.repository.CommentRepository;
import channeling.be.domain.idea.domain.repository.IdeaRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.repository.MemberRepository;
import channeling.be.domain.memberAgree.domain.repository.MemberAgreeRepository;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 영구 삭제 스케줄러
 * - 탈퇴 후 30일이 지난 회원의 데이터를 영구 삭제합니다.
 * - IdeaLog, ReportLog 등 로그 데이터는 보존합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberCleanupScheduler {

    private final MemberRepository memberRepository;
    private final MemberAgreeRepository memberAgreeRepository;
    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final IdeaRepository ideaRepository;
    private final ReportRepository reportRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final TrendKeywordRepository trendKeywordRepository;
    private final RedisUtil redisUtil;

    // 탈퇴 후 데이터 보존 기간 (일)
    private static final int RETENTION_DAYS = 30;

    /**
     * 매일 새벽 3시에 실행
     * 탈퇴 후 30일이 지난 회원의 데이터를 영구 삭제
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupWithdrawnMembers() {
        log.info("---------------[스케줄러] 회원 영구 삭제 스케줄러 시작----------------");

        LocalDateTime threshold = LocalDateTime.now().minusDays(RETENTION_DAYS);
        List<Member> membersToDelete = memberRepository.findAllByStatusAndDeletedAtBefore(
                MemberStatus.WITHDRAWN,
                threshold
        );

        if (membersToDelete.isEmpty()) {
            log.info("영구 삭제 대상 회원 없음");
            return;
        }

        log.info("영구 삭제 대상 회원 수: {}", membersToDelete.size());

        for (Member member : membersToDelete) {
            try {
                deleteMemberData(member);
                log.info("회원 영구 삭제 완료: memberId={}, googleId={}", member.getId(), member.getGoogleId());
            } catch (Exception e) {
                log.error("회원 영구 삭제 실패: memberId={}, error={}", member.getId(), e.getMessage(), e);
            }
        }

        log.info("회원 영구 삭제 스케줄러 완료");
    }

    /**
     * 회원 관련 데이터 삭제
     * 삭제 순서: Comment -> Task -> Report -> Idea -> TrendKeyword -> Video -> Channel -> MemberAgree -> Member
     * 로그 데이터(IdeaLog, ReportLog)는 보존
     */
    private void deleteMemberData(Member member) {
        Long memberId = member.getId();

        // 1. Channel 조회
        channelRepository.findByMember(member).ifPresent(channel -> {
            Long channelId = channel.getId();

            // 2. Report 관련 데이터 삭제 (Comment, Task)
            List<Report> reports = reportRepository.findAllByChannelId(channelId);
            for (Report report : reports) {
                // Comment 삭제
                commentRepository.deleteAllByReportId(report.getId());
                // Task 삭제
                taskRepository.deleteTaskByReportId(report.getId());
            }

            // 3. Report 삭제
            reportRepository.deleteAll(reports);

            // 4. Idea 삭제
            ideaRepository.deleteAllByChannelId(channelId);

            // 5. TrendKeyword 삭제
            trendKeywordRepository.deleteAllByChannelId(channelId);

            // 6. Video 삭제
            videoRepository.deleteAllByChannelId(channelId);

            // 7. Channel 삭제
            channelRepository.delete(channel);
        });

        // 8. MemberAgree 삭제
        memberAgreeRepository.deleteAllByMemberId(memberId);

        // 9. Redis 데이터 삭제
        redisUtil.deleteData("GOOGLE_AT_" + memberId);

        // 10. Member 삭제
        memberRepository.delete(member);
    }
}
