package channeling.be.domain.video.presentaion;

import java.time.LocalDateTime;

import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;

public class VideoResDTO {
	/**
	 *비디오와 관련된 응답을 정의하는 DTO 클래스입니다.
	 **/
	public record VideoBrief(
		//비디오의 간략한 정보를 담고 있는 레코드 클래스입니다.
		Long videoId,
		String videoTitle,
		String  videoThumbnailUrl,
		VideoCategory videoCategory,
		Long viewCount,
		LocalDateTime updatedAt
	){
		public static VideoBrief from(Video video) {
			return new VideoBrief(
				video.getId(),
				video.getTitle(),
				video.getThumbnail(),
				video.getVideoCategory(),
				video.getView(),
				video.getUpdatedAt()
			);
		}
	}
}
