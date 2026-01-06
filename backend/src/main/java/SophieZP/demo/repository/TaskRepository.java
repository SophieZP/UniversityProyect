package SophieZP.demo.repository;

import SophieZP.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // To see the tasks for a especific Subject
    List<Task> findBySubjectId(Long subjectId);

    // To see the GLOBAL SHEDULE:
    List<Task> findBySubject_UserId(Long userId);

    // To order by date
    List<Task> findBySubject_UserIdOrderByDueDateAsc(Long userId);
}