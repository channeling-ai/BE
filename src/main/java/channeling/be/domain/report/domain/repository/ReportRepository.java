package channeling.be.domain.report.domain.repository;

import channeling.be.domain.report.domain.Report;
import channeling.be.domain.video.domain.VideoCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {


	Page<Report> findByVideoChannelIdAndVideoVideoCategoryOrderByUpdatedAtDesc(
		Long channelId,
		VideoCategory category,
		Pageable pageable
	);


}

