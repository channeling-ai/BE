package channeling.be.domain.report.domain;

import channeling.be.domain.log.ReportLog;
import channeling.be.domain.log.LogConvertor;
import channeling.be.domain.log.ReportLogRepository;
import channeling.be.global.config.SpringContextHolder;
import jakarta.persistence.PreRemove;

public class ReportEntityListener {
    @PreRemove
    public void preRemove(Report report) {
        ReportLogRepository repository = SpringContextHolder.getBean(ReportLogRepository.class);

        ReportLog log = LogConvertor.convertToReportLog(report);
        repository.save(log);
    }
}
