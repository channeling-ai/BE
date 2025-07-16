package channeling.be.domain.video.domain;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.global.infrastructure.youtube.YoutubeVideoDTO;

public class VideoConverter {
	public static Video toVideo(YoutubeVideoDTO dto, Channel channel) {
		return Video.builder()
			.channel(channel)
			.youtubeVideoId(dto.getVideoId())
			.view(dto.getViewCount())
			.likeCount(dto.getLikeCount())
			.commentCount(dto.getCommentCount())
			.thumbnail(dto.getThumbnailUrl())
			.title(dto.getTitle())
			.description(dto.getDescription())
			.link("https://www.youtube.com/watch?v="+dto.getVideoId())
			.uploadDate(OffsetDateTime.parse(dto.getUploadDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME
			).toLocalDateTime()) // 업로드 날짜는 ISO 8601 형식으로 제공된다고 가정
			.videoCategory(VideoCategory.LONG) //TODO: 추후 유튜브 API로부터 long, short 여부를 받아와야 함
			.build();
	}
}
