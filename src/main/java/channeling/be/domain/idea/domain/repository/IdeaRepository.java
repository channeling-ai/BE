package channeling.be.domain.idea.domain.repository;

import channeling.be.domain.idea.domain.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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


    @Modifying
    @Query("""
    DELETE
    FROM Idea i
    WHERE i.channel.member.id = :memberId AND i.isBookMarked = false
 """)
    int deleteAllByMemberWithoutBookmarked(@Param("memberId")  Long memberId);

    @Query("""
    SELECT i
    FROM Idea i 
    WHERE i.channel.member.id = :memberId AND i.isBookMarked = false
    """)
    List<Idea> findByMemberWithoutBookmarked(@Param("memberId") Long memberId);


    @Query("""
    SELECT i
    FROM Idea i
    JOIN i.channel c
    WHERE i.createdAt > :loginTime
    AND c.id = :channelId
    ORDER BY i.createdAt DESC, i.id DESC
    """)
    List<Idea> findByIdeaFirstCursor(@Param("loginTime") LocalDateTime loginTime,
                                     @Param("channelId") Long channelId,
                                     Pageable pageable);

    @Query("""
    SELECT i
    FROM Idea i
    JOIN i.channel c
    WHERE i.createdAt > :loginTime
    AND c.id = :channelId 
    AND (i.createdAt < :cursorTime 
        OR (i.createdAt = :cursorTime AND i.id < :cursorId))
    ORDER BY i.createdAt DESC, i.id DESC
    """)
    List<Idea> findByIdeaAfterCursor(@Param("loginTime") LocalDateTime loginTime,
                                     @Param("channelId") Long channelId,
                                     @Param("cursorId") Long cursorId,
                                     @Param("cursorTime") LocalDateTime cursorTime,
                                     Pageable pageable);

    @Query("""
	SELECT COUNT(i)
	FROM Idea i
	WHERE i.channel.member.id = :memberId
	  AND i.createdAt >= :start
	""")
    long countMonthlyIdeas(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start
    );

}


