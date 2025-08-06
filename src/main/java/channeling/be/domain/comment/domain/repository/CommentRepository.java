package channeling.be.domain.comment.domain.repository;

import channeling.be.domain.comment.domain.Comment;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findTop5ByReportAndCommentType(Report report, CommentType commentType);
}
