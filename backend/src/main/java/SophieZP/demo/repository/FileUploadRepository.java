package SophieZP.demo.repository;

import SophieZP.demo.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    // Find all files uploaded for a specific subject
    List<FileUpload> findBySubjectId(Long subjectId);
    
    // Find all files uploaded by a specific user
    List<FileUpload> findByUserId(Long userId);
    
    // Find all files in a subject uploaded by a specific user
    List<FileUpload> findBySubjectIdAndUserId(Long subjectId, Long userId);
}
