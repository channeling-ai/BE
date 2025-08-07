package channeling.be.domain.report.application;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.comment.domain.repository.CommentRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.report.presentation.ReportConverter;
import channeling.be.domain.report.presentation.ReportResDto;
import channeling.be.domain.task.domain.Task;
import channeling.be.domain.task.domain.TaskStatus;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.CustomException;
import channeling.be.response.exception.handler.ReportHandler;
import channeling.be.response.exception.handler.TaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportServiceImpl implements ReportService {
    private final TaskRepository taskRepository;
	private final ReportRepository reportRepository;
	private final CommentRepository commentRepository;
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
    public Report checkReport(Long reportId, Member member) {
        // TODO 태스크 삭제하지 않는다고 가정
        Task task = taskRepository.findByReportId(reportId)
                .orElseThrow(() -> new TaskHandler(ErrorStatus._REPORT_NOT_TASK));

        if (task.getAnalysisStatus() != TaskStatus.COMPLETED)
            throw new TaskHandler(ErrorStatus._REPORT_NOT_ANALYTICS);

        if (task.getOverviewStatus() != TaskStatus.COMPLETED)
            throw new TaskHandler(ErrorStatus._REPORT_NOT_OVERVIEW);

        if (task.getIdeaStatus() != TaskStatus.COMPLETED)
            throw new TaskHandler(ErrorStatus._REPORT_NOT_IDEA);

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_FOUND));
        return reportRepository.findByReportAndMember(report.getId(), member.getId()).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_MEMBER));
    }
}
