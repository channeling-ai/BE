package channeling.be.domain.idea.domain.event;


import channeling.be.domain.log.domain.IdeaLog;
import channeling.be.domain.log.repository.IdeaLogRepository;
import channeling.be.domain.log.LogConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdeaEntityHandler {
    private final IdeaLogRepository ideaLogRepository;

    @TransactionalEventListener
    public void preRemove(IdeaDeletedEvent event) {
        log.info("아이디어 삭제 이벤트 수신 - ID: {}", event.idea().getId());

        IdeaLog log = LogConvertor.convertToIdeaLog(event.idea());
        ideaLogRepository.save(log);
    }
}
