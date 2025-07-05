package channeling.be.domain.video.domain.repository;

import channeling.be.domain.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {

}
