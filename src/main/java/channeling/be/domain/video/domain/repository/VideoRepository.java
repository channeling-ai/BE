package channeling.be.domain.video.domain.repository;

import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;


public interface VideoRepository extends JpaRepository<Video, Long> {

	Slice<Video> findByChannelIdAndVideoCategory(Long channelId, VideoCategory videoCategory, Pageable pageable);

	Slice<Video> findByChannelIdAndVideoCategoryOrderByUploadDateDesc(Long channelId, VideoCategory type, Pageable pageable);

	Slice<Video> findByChannelIdAndVideoCategoryAndUploadDateLessThanOrderByUploadDateDesc(Long channelId, VideoCategory type, LocalDateTime cursor, Pageable pageable);

	Optional<Video> findByYoutubeVideoId(String youtubeVideoId);

	// 내 채널 추천 영상 : 조회 수 높은 영상, 리포트 안 받아본 영상
	@Query("SELECT v FROM Video v " +
			"left join fetch Report r on v.id = r.video.id " +
			"WHERE v.channel.id = :channelId " +
			"AND r.id is null " +
			"order by v.view desc ")
	Page<Video> findAllRecommendationByChannel(Long channelId, Pageable pageable);
}
