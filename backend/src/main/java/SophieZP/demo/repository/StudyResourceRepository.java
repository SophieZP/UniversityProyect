package SophieZP.demo.repository;

import SophieZP.demo.entity.StudyResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyResourceRepository extends JpaRepository<StudyResource, Long> {
    List<StudyResource> findBySubjectId(Long subjectId);
}