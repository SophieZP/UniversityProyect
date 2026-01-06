package SophieZP.demo.repository;

import SophieZP.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Search all the Subjects asociated by User
    List<Subject> findByUserId(Long userId);
}