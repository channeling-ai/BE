package channeling.be.domain.auth.application;

import channeling.be.domain.TrendKeyword.service.TrendKeywordService;
import channeling.be.domain.channel.application.ChannelStatsService;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.idea.application.IdeaService;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.video.application.VideoSyncService;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginPostProcessor {

    private final VideoSyncService videoSyncService;
    private final ChannelStatsService channelStatsService;
    private final IdeaService ideaService;
    private final TrendKeywordService trendKeywordService;
    private final RedisUtil redisUtil;

    @Async
    public void executeAsync(Member member, Channel channel,
                             String googleAccessToken, boolean isNew) {
        tryStep("syncVideos", member.getId(), () ->
            videoSyncService.syncVideos(channel, googleAccessToken));

        // 신규 채널은 생성 시 이미 통계가 세팅되므로 스킵
        if (!isNew) {
            tryStep("updateStats", member.getId(), () ->
                channelStatsService.updateChannelStats(channel, googleAccessToken));
        }

        tryStep("trendKeyword", member.getId(), () ->
            trendKeywordService.updateChannelTrendKeyword(member));

        tryStep("cleanupIdeas", member.getId(), () ->
            ideaService.deleteNotBookMarkedIdeas(member));
    }

    private void tryStep(String step, Long userId, Runnable action) {
        try {
            action.run();
            log.info("{} 완료 - userId: {}", step, userId);
        } catch (Exception e) {
            log.error("{} 실패 - userId: {}", step, userId, e);
            redisUtil.publishFailure(userId, step);
        }
    }
}
