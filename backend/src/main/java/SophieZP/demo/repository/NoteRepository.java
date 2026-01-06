package SophieZP.demo.repository;

import SophieZP.demo.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Find all notes by Subject
    List<Note> findBySubjectId(Long subjectId);
    
    // Find all notes by User
    List<Note> findByUserId(Long userId);
    
    // Find all notes by Subject and User
    List<Note> findBySubjectIdAndUserId(Long subjectId, Long userId);
}
