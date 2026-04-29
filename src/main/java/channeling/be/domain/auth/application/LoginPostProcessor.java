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

    private static final String STEP_SYNC_VIDEOS = "syncVideos";
    private static final String STEP_UPDATE_STATS = "updateStats";
    private static final String STEP_TREND_KEYWORD = "trendKeyword";
    private static final String STEP_CLEANUP_IDEAS = "cleanupIdeas";

    private final VideoSyncService videoSyncService;
    private final ChannelStatsService channelStatsService;
    private final IdeaService ideaService;
    private final TrendKeywordService trendKeywordService;

    @Async("asyncExecutor")
    public void executeAsync(Member member, Channel channel,
                             String googleAccessToken, boolean isNew) {
        tryStep(STEP_SYNC_VIDEOS, member.getId(), () ->
            videoSyncService.syncVideos(channel, googleAccessToken));

        if (!isNew) {
            tryStep(STEP_UPDATE_STATS, member.getId(), () ->
                channelStatsService.updateChannelStats(channel, googleAccessToken));
        }

        tryStep(STEP_TREND_KEYWORD, member.getId(), () ->
            trendKeywordService.updateChannelTrendKeyword(member));

        tryStep(STEP_CLEANUP_IDEAS, member.getId(), () ->
            ideaService.deleteNotBookMarkedIdeas(member));
    }

    private void tryStep(String step, Long userId, Runnable action) {
        long startTime = System.currentTimeMillis();
        try {
            action.run();
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("{} 완료 - userId: {}, elapsed: {}ms", step, userId, elapsed);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("{} 실패 - userId: {}, elapsed: {}ms", step, userId, elapsed, e);
        }
    }
}
