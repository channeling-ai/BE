package channeling.be.domain.comment.domain.repository;

import channeling.be.domain.comment.domain.Comment;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findTop5ByReportAndCommentType(Report report, CommentType commentType);

    @Modifying(clearAutomatically = true)
    @Query("""
    DELETE
    FROM Comment cm
    WHERE cm.report.id = :reportId AND cm.report.video.channel.member.id = :memberId
""")
    void deleteAllByReportAndMember(@Param("reportId") Long reportId, @Param("memberId") Long memberId);

    List<Comment> findByReport(Report report);
}