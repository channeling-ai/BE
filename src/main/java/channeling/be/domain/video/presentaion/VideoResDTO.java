package channeling.be.domain.video.presentaion;

import java.time.LocalDateTime;

import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public class VideoResDTO {
	/**
	 *비디오와 관련된 응답을 정의하는 DTO 클래스입니다.
	 **/
	public record VideoBrief (
		//비디오의 간략한 정보를 담고 있는 레코드 클래스입니다.
		Long videoId,
		String videoTitle,
		String  videoThumbnailUrl,
		VideoCategory videoCategory,
		Long viewCount,
		LocalDateTime uploadDate
	) {
		public static VideoBrief from(Video video) {
			return new VideoBrief(
				video.getId(),
				video.getTitle(),
				video.getThumbnail(),
				video.getVideoCategory(),
				video.getView(),
				video.getUploadDate()
			);
		}
	}

	public record VideoRecommend (
			Long videoId,
			String videoTitle,
			String  videoThumbnailUrl,
			VideoCategory videoCategory,
			Long viewCount,
			LocalDateTime uploadDate,
			String channelName,
			String channelImage
	) {
		public static VideoRecommend from(Video video) {
			return new VideoRecommend(
					video.getId(),
					video.getTitle(),
					video.getThumbnail(),
					video.getVideoCategory(),
					video.getView(),
					video.getUploadDate(),
					video.getChannel().getName(),
					video.getChannel().getImage()
			);
		}
	}

	public record ReportVideoInfo (
		@Schema(description = "비디오 ID")
		Long videoId,
		@Schema(description = "유튜브 비디오 ID")
		String youtubeVideoId,
		@Schema(description = "비디오 제목")
		String videoTitle,
		@Schema(description = "비디오 썸네일 URL")
		String  videoThumbnailUrl,
		@Schema(description = "비디오 카테고리")
		VideoCategory videoCategory,
		@Schema(description = "비디오 조회수")
		Long viewCount,
		@Schema(description = "비디오 업로드 날짜")
		LocalDateTime videoCreatedDate,
		@Schema(description = "채널 이름")
		String ChannelName,
		@Schema(description = "채널 마지막 업데이트 날짜")
		LocalDateTime lastUpdatedDate
	) {
		public static ReportVideoInfo from(Video video) {
			return new ReportVideoInfo(
				video.getId(),
				video.getYoutubeVideoId(),
				video.getTitle(),
				video.getThumbnail(),
				video.getVideoCategory(),
				video.getView(),
				video.getUploadDate().plusHours(9L), // 한국 시간으로 변환
				video.getChannel().getName(),
				video.getChannel().getChannelUpdateAt()
			);
		}
	}
}
