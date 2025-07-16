package channeling.be.global.infrastructure.youtube;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class YoutubePlayListResDTO {
	private String nextPageToken;
	private List<Item> items;

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Getter
	public static class Item {
		private Snippet snippet;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Snippet {
		private String title;
		private String publishedAt;
		private ResourceId resourceId;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResourceId {
		private String videoId;
	}
}
