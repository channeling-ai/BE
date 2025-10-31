package channeling.be.domain.report.application;

import channeling.be.domain.comment.domain.repository.CommentRepository;
import channeling.be.domain.idea.domain.repository.IdeaRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.domain.video.domain.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReportDeleteService {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public void deleteExistingReport(Report report, Video video, Member member) {
        log.info("기존 리포트 삭제 시작 - reportId: {}", report.getId());

        // 연관된 댓글 리스트 삭제
        commentRepository.deleteAllByReportAndMember(report.getId(), member.getId());
        // 연관된 task 삭제
        taskRepository.deleteTaskByReportId(report.getId());
        // 리포트 삭제
        reportRepository.deleteById(report.getId());

        // 명시적 flush로 삭제 즉시 실행
        reportRepository.flush();
        log.info("기존 리포트 삭제 완료 - reportId: {}", report.getId());
    }
}