package channeling.be.domain.channel.domain.repository;

import java.util.Optional;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

	Optional<Channel> findByMember(Member member);
}
