package com.platformzeta.storage.repository;

import com.platformzeta.storage.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * User repository, uses standard JpaRepository and specific for searching bu userId and modifying data
 */
@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
    List<StoredFile> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE StoredFile s SET 
            s.fileTitle = :file_title,
            s.fileDescription = :file_description,
            s.fileName = :file_name,
            s.fileExtension = :file_extension,
            s.fileData = :file_data,
            s.updatedAt = :updated_at,
            s.updatedBy = :updated_by
        WHERE s.id = :id
    """)
    int updateStoredFile(
            @Param("id") Long id,
            @Param("file_title") String fileTitle,
            @Param("file_description") String fileDescription,
            @Param("file_name") String fileName,
            @Param("file_extension") String fileExtension,
            @Param("file_data") byte[] fileData,
            @Param("updated_at") Instant updatedAt,
            @Param("updated_by") String updatedBy
    );

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE StoredFile s SET 
            s.fileTitle = :file_title,
            s.fileDescription = :file_description,
            s.updatedAt = :updated_at,
            s.updatedBy = :updated_by
        WHERE s.id = :id
    """)
    int updateStoredFileDetails(
            @Param("id") Long id,
            @Param("file_title") String fileTitle,
            @Param("file_description") String fileDescription,
            @Param("updated_at") Instant updatedAt,
            @Param("updated_by") String updatedBy
    );
}