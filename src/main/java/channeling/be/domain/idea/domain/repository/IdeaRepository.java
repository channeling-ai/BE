package channeling.be.domain.idea.domain.repository;

import channeling.be.domain.idea.domain.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

}
