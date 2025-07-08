package channeling.be.domain.video.domain.repository;

import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {

	Slice<Video> findByChannelIdAndVideoCategory(Long channelId, VideoCategory videoCategory, Pageable pageable);
}
