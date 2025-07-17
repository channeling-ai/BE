package channeling.be.domain.idea.domain.repository;

import channeling.be.domain.idea.domain.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
    @Query("""
    SELECT i
    FROM Idea i
    JOIN FETCH i.video v
    JOIN FETCH v.channel c
    JOIN FETCH c.member m
    WHERE i.id = :ideaId
    """)
    Optional<Idea> findWithVideoChannelMemberById(@Param("ideaId") Long ideaId);

}
