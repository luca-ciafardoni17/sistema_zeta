package com.platformzeta.storage.transform;

import com.platformzeta.storage.dto.StoredFileDetailsDto;
import com.platformzeta.storage.dto.StoredFileDto;
import com.platformzeta.storage.dto.StoredFileRequestDto;
import com.platformzeta.storage.entity.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

public class TransformUtils {

    public static StoredFile transformDtoToEntity(StoredFileRequestDto storedFileRequestDto, MultipartFile file, String email) {
        StoredFile storedFile = new StoredFile();
        if (file != null && !file.isEmpty()) {
            try {
                storedFile.setFileData(file.getBytes());
                storedFile.setFileName(file.getOriginalFilename());
                storedFile.setFileExtension(file.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: ", e);
            }
        }
        storedFile.setFileTitle(storedFileRequestDto.fileTitle());
        storedFile.setFileDescription(storedFileRequestDto.fileDescription());
        storedFile.setCreatedBy(email);
        storedFile.setCreatedAt(Instant.now());
        return storedFile;
    }

    public static StoredFileDto transformEntityToDto(StoredFile storedFile) {
        return new StoredFileDto(
                storedFile.getFileTitle(),
                storedFile.getFileDescription(),
                storedFile.getFileName(),
                storedFile.getFileExtension(),
                storedFile.getFileData()
        );
    }

    public static StoredFileDetailsDto transformEntityToDtoDetails(StoredFile storedFile) {
        return new StoredFileDetailsDto(
                storedFile.getId(),
                storedFile.getFileTitle(),
                storedFile.getFileDescription(),
                storedFile.getFileName(),
                storedFile.getFileExtension()
        );
    }
}
