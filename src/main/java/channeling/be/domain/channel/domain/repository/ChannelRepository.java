package channeling.be.domain.channel.domain.repository;

import channeling.be.domain.channel.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
