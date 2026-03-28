package com.platformzeta.storage.service;

import com.platformzeta.storage.dto.StoredFileDetailsDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface IStoredFileService {

    /**
     * @param storedFileRequestJson - file to be stored name and description
     * @param file - file to be stored
     * @return - boolean representing result of operation
     */
    boolean createStoredFile(String storedFileRequestJson, MultipartFile file);

    /**
     * @param id - file stored id
     * @param binaryData - boolean true = file, false = details
     * @return file or StoredFileDetailsDto
     * @throws AccessDeniedException
     */
    Optional<?> getStoredFile(Long id, Boolean binaryData) throws AccessDeniedException;

    /**
     * Returns all file details for a given user from jwtToken
     * @return
     */
    Optional<List<StoredFileDetailsDto>> getStoredFilesDetail();

    /**
     * @param id - file stored id
     * @param storedFileRequestJson - new file title or description
     * @param file - optional new file for replacement
     * @return - boolean representing result of operation
     * @throws IOException
     */
    boolean modifyStoredFile(Long id, String storedFileRequestJson, MultipartFile file) throws IOException;

    /**
     * @param id - file stored id
     * @throws AccessDeniedException
     */
    void deleteStoredFile(Long id) throws AccessDeniedException;
}
