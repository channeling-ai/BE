package channeling.be.global.infrastructure.youtube;

import java.time.LocalDateTime;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YoutubeVideoDTO {
	private final String videoId;
	private final long viewCount;
	private final long likeCount;
	private final long commentCount;
	private final String thumbnailUrl;
	private final String title;
	private final String description;
	private final String uploadDate;
}
