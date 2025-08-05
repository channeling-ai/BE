package channeling.be.domain.report.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.presentation.ReportConverter;
import channeling.be.domain.report.presentation.ReportResDto;
import channeling.be.domain.task.domain.Task;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.TaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportServiceImpl implements ReportService {
    private final TaskRepository taskRepository;

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
}
