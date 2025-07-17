package channeling.be.global.infrastructure.youtube.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class YoutubeVideoDetailDTO {
	private final String description;
	private final String categoryId;
	private final Long viewCount;
	private final Long likeCount;
	private final Long commentCount;
}
