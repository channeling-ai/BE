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
    JOIN FETCH i.video v
    JOIN FETCH v.channel c
    JOIN FETCH c.member m
    WHERE i.id = :ideaId
    """)
    Optional<Idea> findWithVideoChannelMemberById(@Param("ideaId") Long ideaId);

    @Query("""
    SELECT i
    FROM Idea i
    JOIN i.video v
    JOIN v.channel c
    JOIN c.member m
    WHERE m.id = :memberId AND i.isBookMarked = true
""")
    Page<Idea> findIdeasByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Modifying
    @Query("""
    DELETE
    FROM Idea i
    WHERE i.video.id = :videoId And i.video.channel.member.id = :memberId AND i.isBookMarked = false
 """)
    void deleteAllByVideoWithoutBookmarked(@Param("videoId") Long videoId, @Param("memberId")  Long memberId);

    @Modifying
    @Query("""
    DELETE
    FROM Idea i
    WHERE i.video.channel.member.id = :memberId AND i.isBookMarked = false
 """)
    int deleteAllByMemberWithoutBookmarked(@Param("memberId")  Long memberId);
}
