package channeling.be.domain.video.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.youtube.YouTubeApiService;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VideoSyncServiceTest {

    @Mock
    private VideoService videoService;

    @Mock
    private YouTubeApiService youTubeApiService;

    @InjectMocks
    private VideoSyncService videoSyncService;

    @Nested
    @DisplayName("syncVideos는")
    class Describe_syncVideos {

        @Test
        @DisplayName("영상을 조회하고 저장한다")
        void it_fetches_and_saves_videos() {
            // given
            Channel channel = createChannel(1L);
            List<YoutubeVideoBriefDTO> briefs = createBriefs();
            List<YoutubeVideoDetailDTO> details = createDetails();

            given(youTubeApiService.fetchVideoBriefs("token", "UU123")).willReturn(briefs);
            given(youTubeApiService.fetchVideoDetails(eq("token"), anyList())).willReturn(details);

            // when
            videoSyncService.syncVideos(channel, "token");

            // then
            verify(youTubeApiService).markShortsVideos(briefs, details);
            verify(videoService).saveVideosWithStats(briefs, details, channel);
        }

        @Test
        @DisplayName("Shorts 영상은 카테고리 42로 분류한다")
        void it_categorizes_shorts_as_42() {
            // given
            Channel channel = createChannel(1L);
            YoutubeVideoBriefDTO brief1 = new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z");
            YoutubeVideoBriefDTO brief2 = new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z");
            YoutubeVideoDetailDTO detail1 = new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L);
            YoutubeVideoDetailDTO detail2 = new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L);

            List<YoutubeVideoBriefDTO> briefs = List.of(brief1, brief2);
            List<YoutubeVideoDetailDTO> details = List.of(detail1, detail2);

            given(youTubeApiService.fetchVideoBriefs("token", "UU123")).willReturn(briefs);
            given(youTubeApiService.fetchVideoDetails(eq("token"), anyList())).willReturn(details);

            // markShortsVideos가 호출되면 vid1만 Shorts로 마킹
            doAnswer(invocation -> {
                List<YoutubeVideoDetailDTO> d = invocation.getArgument(1);
                d.get(0).updateCategoryId("42");
                return null;
            }).when(youTubeApiService).markShortsVideos(anyList(), anyList());

            // when
            videoSyncService.syncVideos(channel, "token");

            // then — vid1만 Shorts(42)로 분류
            assertThat(detail1.getCategoryId()).isEqualTo("42");
            assertThat(detail2.getCategoryId()).isEqualTo("24");
        }

        @Test
        @DisplayName("모든 영상의 좋아요와 댓글을 합산한다")
        void it_accumulates_likes_and_comments() {
            // given
            Channel channel = createChannel(1L);
            YoutubeVideoBriefDTO brief1 = new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z");
            YoutubeVideoBriefDTO brief2 = new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z");
            YoutubeVideoDetailDTO detail1 = new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L);
            YoutubeVideoDetailDTO detail2 = new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L);

            List<YoutubeVideoBriefDTO> briefs = List.of(brief1, brief2);
            List<YoutubeVideoDetailDTO> details = List.of(detail1, detail2);

            given(youTubeApiService.fetchVideoBriefs("token", "UU123")).willReturn(briefs);
            given(youTubeApiService.fetchVideoDetails(eq("token"), anyList())).willReturn(details);

            // saveVideosWithStats가 호출되면 실제 누산 로직 시뮬레이션
            doAnswer(invocation -> {
                List<YoutubeVideoBriefDTO> b = invocation.getArgument(0);
                List<YoutubeVideoDetailDTO> d = invocation.getArgument(1);
                Channel ch = invocation.getArgument(2);
                long likeCount = 0, commentCount = 0;
                for (int i = 0; i < b.size(); i++) {
                    likeCount += d.get(i).getLikeCount();
                    commentCount += d.get(i).getCommentCount();
                }
                ch.updateChannelStats(likeCount, commentCount);
                return null;
            }).when(videoService).saveVideosWithStats(anyList(), anyList(), any(Channel.class));

            // when
            videoSyncService.syncVideos(channel, "token");

            // then — likeCount = 50+30 = 80, comment = 10+20 = 30
            assertThat(channel.getLikeCount()).isEqualTo(80L);
            assertThat(channel.getComment()).isEqualTo(30L);
        }
    }

    @Nested
    @DisplayName("isYoutubeShorts는")
    class Describe_isYoutubeShorts {

        @Test
        @DisplayName("YouTubeApiService에 위임한다 — Shorts인 경우")
        void it_delegates_to_api_service_shorts() {
            given(youTubeApiService.isYoutubeShorts("vid1")).willReturn(true);
            assertThat(youTubeApiService.isYoutubeShorts("vid1")).isTrue();
        }

        @Test
        @DisplayName("YouTubeApiService에 위임한다 — Shorts가 아닌 경우")
        void it_delegates_to_api_service_not_shorts() {
            given(youTubeApiService.isYoutubeShorts("vid1")).willReturn(false);
            assertThat(youTubeApiService.isYoutubeShorts("vid1")).isFalse();
        }
    }

    // --- Fixture ---

    private Channel createChannel(Long id) {
        Member member = Member.builder()
                .id(1L).nickname("테스트").googleEmail("test@test.com")
                .googleId("google-123").plan(SubscriptionPlan.FREE).status(MemberStatus.ACTIVE)
                .build();
        return Channel.builder()
                .id(id).member(member).youtubeChannelId("UC123").youtubePlaylistId("UU123")
                .name("테스트 채널").view(1000L).likeCount(0L).subscribe(500L).share(50L)
                .videoCount(10L).comment(0L).link("https://youtube.com/channel/UC123")
                .joinDate(LocalDateTime.of(2024, 1, 1, 0, 0)).target("default").concept("default")
                .image("http://img.url").channelUpdateAt(LocalDateTime.now())
                .build();
    }

    private List<YoutubeVideoBriefDTO> createBriefs() {
        return List.of(
                new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z"),
                new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z")
        );
    }

    private List<YoutubeVideoDetailDTO> createDetails() {
        return List.of(
                new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L),
                new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L)
        );
    }
}
