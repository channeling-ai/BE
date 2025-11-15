package channeling.be.domain.idea.domain;


import channeling.be.domain.log.IdeaLog;
import channeling.be.domain.log.IdeaLogRepository;
import channeling.be.domain.log.LogConvertor;
import channeling.be.global.config.SpringContextHolder;
import jakarta.persistence.PreRemove;

public class IdeaEntityListener {
    @PreRemove
    public void preRemove(Idea idea) {
        IdeaLogRepository repository = SpringContextHolder.getBean(IdeaLogRepository.class);

        IdeaLog log = LogConvertor.convertToIdeaLog(idea);
        repository.save(log);
    }
}
