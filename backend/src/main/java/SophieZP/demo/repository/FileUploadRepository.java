package SophieZP.demo.repository;

import SophieZP.demo.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    /**
     * Encuentra todos los archivos de una materia específica
     */
    List<FileUpload> findBySubjectId(Long subjectId);

    /**
     * Encuentra todos los archivos subidos por un usuario
     */
    List<FileUpload> findByUserId(Long userId);

    /**
     * Encuentra todos los archivos de una materia subidos por un usuario
     */
    List<FileUpload> findBySubjectIdAndUserId(Long subjectId, Long userId);

    /**
     * Encuentra archivos por tipo
     */
    List<FileUpload> findByFileType(String fileType);
}