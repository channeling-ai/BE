package channeling.be.domain.member.domain.repository;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByGoogleId(String googleId);

    /**
     * 특정 상태이고 deletedAt이 특정 시간 이전인 회원 조회 (영구 삭제 대상)
     */
    @Query("SELECT m FROM Member m WHERE m.status = :status AND m.deletedAt < :dateTime")
    List<Member> findAllByStatusAndDeletedAtBefore(
            @Param("status") MemberStatus status,
            @Param("dateTime") LocalDateTime dateTime
    );
}
