package channeling.be.domain.task.domain.repository;

import channeling.be.domain.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
