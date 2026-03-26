package com.platformzeta.storage.service;

import com.platformzeta.storage.dto.StoredFileDetailsDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IStoredFileService {

    boolean createStoredFile(String storedFileRequestJson, MultipartFile file);

    Optional<?> getStoredFile(Long id, Boolean binaryData);
    Optional<List<StoredFileDetailsDto>> getStoredFilesDetail();

    boolean modifyStoredFile(Long id, String storedFileRequestJson, MultipartFile file) throws IOException;

    void deleteStoredFile(Long id);
}
