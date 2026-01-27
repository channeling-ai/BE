package channeling.be.domain.memberAgree.domain.repository;

import channeling.be.domain.memberAgree.domain.MemberAgree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberAgreeRepository extends JpaRepository<MemberAgree, Long> {
    Optional<MemberAgree> findByMemberId(@Param("memberId") Long memberId);

    void deleteAllByMemberId(Long memberId);
}
