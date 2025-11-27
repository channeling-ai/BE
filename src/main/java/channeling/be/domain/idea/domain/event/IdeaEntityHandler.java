package channeling.be.domain.idea.domain.event;


import channeling.be.domain.log.IdeaLog;
import channeling.be.domain.log.IdeaLogRepository;
import channeling.be.domain.log.LogConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdeaEntityHandler {
    private final IdeaLogRepository ideaLogRepository;

    @EventListener
    public void preRemove(IdeaDeletedEvent event) {
        log.info("아이디어 삭제 이벤트 수신 - ID: {}", event.idea().getId());

        IdeaLog log = LogConvertor.convertToIdeaLog(event.idea());
        ideaLogRepository.save(log);
    }
}
