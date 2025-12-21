package channeling.be.domain.report.application;

import channeling.be.domain.comment.domain.Comment;
import channeling.be.domain.comment.domain.repository.CommentRepository;
import channeling.be.domain.log.LogConvertor;
import channeling.be.domain.log.domain.DeleteType;
import channeling.be.domain.log.domain.ReportLog;
import channeling.be.domain.log.repository.ReportLogRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.task.domain.Task;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.domain.video.domain.Video;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.TaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReportDeleteService {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ReportLogRepository reportLogRepository;

    @Transactional
    public void deleteExistingReport(Report report, Video video, Member member, DeleteType type) {
        log.info("기존 리포트 삭제 시작 - reportId: {}", report.getId());

        // 리포트 로그 저장
        Task task = taskRepository.findByReportId(report.getId())
                .orElseThrow(() -> new TaskHandler(ErrorStatus._TASK_NOT_FOUND));
        List<Comment> comments = commentRepository.findByReport(report);

        ReportLog log = LogConvertor.convertToReportLog(type, report, task, comments);
        reportLogRepository.save(log);

        // 연관된 댓글 리스트 삭제
        commentRepository.deleteAllByReportAndMember(report.getId(), member.getId());
        // 연관된 task 삭제
        taskRepository.deleteTaskByReportId(report.getId());
        // 리포트 삭제
        reportRepository.deleteById(report.getId());

        // 명시적 flush로 삭제 즉시 실행
        reportRepository.flush();
    }
}