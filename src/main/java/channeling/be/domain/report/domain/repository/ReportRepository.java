package channeling.be.domain.report.domain.repository;

import channeling.be.domain.report.domain.Report;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("""
    SELECT r
    FROM Report r
    JOIN r.video v
    JOIN v.channel c
    JOIN c.member m
    WHERE m.id = :memberId AND r.id = :reportId
""")
    Optional<Report> findByReportAndMember(@Param("reportId") Long reportId, @Param("memberId") Long memberId);

    @Query("""
    SELECT r
    FROM Report r
    JOIN r.video v
    JOIN v.channel c
    JOIN c.member m
    WHERE m.id = :memberId AND v.id = :videoId
""")
    Optional<Report> findByVideoAndMember(@Param("videoId") Long videoId, @Param("memberId") Long memberId);


	Page<Report> findByVideoChannelIdAndVideoVideoCategoryOrderByUpdatedAtDesc(
		Long channelId,
		VideoCategory category,
		Pageable pageable
	);


}
