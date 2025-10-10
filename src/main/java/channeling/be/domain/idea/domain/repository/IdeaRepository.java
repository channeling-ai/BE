package channeling.be.domain.idea.domain.repository;

import channeling.be.domain.idea.domain.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
    @Query("""
    SELECT i
    FROM Idea i
    JOIN FETCH i.channel c
    JOIN FETCH c.member m
    WHERE i.id = :ideaId
    """)
    Optional<Idea> findWithVideoChannelMemberById(@Param("ideaId") Long ideaId);

    @Query("""
    SELECT i
    FROM Idea i
    JOIN i.channel c
    JOIN c.member m
    WHERE m.id = :memberId AND i.isBookMarked = true
""")
    Page<Idea> findIdeasByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
