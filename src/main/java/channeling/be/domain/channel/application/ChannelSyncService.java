package channeling.be.domain.channel.application;

import channeling.be.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 채널 동기화를 비동기로 처리하는 서비스.
 * 로그인 시 YouTube API 호출을 백그라운드에서 수행하여 로그인 응답 시간을 단축합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelSyncService {

    private final ChannelService channelService;

    /**
     * 비동기로 채널 정보를 YouTube API와 동기화합니다.
     * 기존 사용자 로그인 시 백그라운드에서 호출됩니다.
     *
     * @param member 동기화할 채널의 소유자
     */
    @Async
    public void syncChannelAsync(Member member) {
        try {
            log.info("채널 비동기 동기화 시작 - memberId: {}", member.getId());
            channelService.updateOrCreateChannelByMember(member);
            log.info("채널 비동기 동기화 완료 - memberId: {}", member.getId());
        } catch (Exception e) {
            log.error("채널 비동기 동기화 실패 - memberId: {}, error: {}", member.getId(), e.getMessage(), e);
        }
    }
}
