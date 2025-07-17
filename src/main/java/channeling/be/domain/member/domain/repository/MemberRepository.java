package channeling.be.domain.member.domain.repository;

import channeling.be.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByGoogleId(String googleId);
}
