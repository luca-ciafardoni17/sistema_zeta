package com.platformzeta.storage.service.impl;

import com.platformzeta.storage.config.security.JwtUtil;
import com.platformzeta.storage.dto.StoredFileDetailsDto;
import com.platformzeta.storage.dto.StoredFileRequestDto;
import com.platformzeta.storage.entity.StoredFile;
import com.platformzeta.storage.repository.StoredFileRepository;
import com.platformzeta.storage.service.IStoredFileService;
import com.platformzeta.storage.transform.TransformUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoredFileServiceImpl implements IStoredFileService {

    private final StoredFileRepository storedFileRepository;

    @Override
    @Transactional
    public boolean createStoredFile(String storedFileRequestJson, MultipartFile file) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if (!email.endsWith("@aruba.it")) {
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        StoredFileRequestDto storedFileRequestDto = objectMapper.readValue(storedFileRequestJson, StoredFileRequestDto.class);
        StoredFile storedFileToSave = TransformUtils.transformDtoToEntity(storedFileRequestDto, file, email);
        storedFileRepository.save(storedFileToSave);
        return true;
    }

    @Override
    public Optional<?> getStoredFile(Long id, Boolean binaryData) {
        Long userId = Long.valueOf(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());
        StoredFile storedFile = storedFileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
        if (userId != storedFile.getUserId()) {
            throw new RuntimeException("Current user is not authorized to download requested file!");
        }
        if (binaryData) {
            return Optional.of(TransformUtils.transformEntityToDto(storedFile));
        } else {
            return Optional.of(TransformUtils.transformEntityToDtoDetails(storedFile));
        }
    }

    @Override
    public Optional<List<StoredFileDetailsDto>> getStoredFilesDetail() {
        Long userId = Long.valueOf(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());
        List<StoredFile> storedFiles = storedFileRepository.findByUserId(userId);
        return Optional.of(
                storedFiles.stream()
                        .map(TransformUtils::transformEntityToDtoDetails)
                        .toList()
        );
    }

    @Override
    @Transactional
    public boolean modifyStoredFile(Long id, String storedFileRequestJson, MultipartFile file) {
        Long userId = Long.valueOf(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());
        StoredFile foundStoredFile = storedFileRepository.findById(id).orElseThrow(() -> new RuntimeException("No file found for given id: " + id));
        if (userId != foundStoredFile.getUserId()) {
            throw new RuntimeException("Current user is not authorized to modify this file");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        StoredFileRequestDto storedFileRequestDto = objectMapper.readValue(storedFileRequestJson, StoredFileRequestDto.class);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if (file != null) {
            try {
                return storedFileRepository.updateStoredFile(
                        id,
                        storedFileRequestDto.fileTitle(),
                        storedFileRequestDto.fileDescription(),
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes(),
                        Instant.now(),
                        email
                ) > 0;
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: ", e);
            }
        } else {
            return storedFileRepository.updateStoredFileDetails(
                    id,
                    storedFileRequestDto.fileTitle(),
                    storedFileRequestDto.fileDescription(),
                    Instant.now(),
                    email
            ) > 0;
        }
    }

    @Override
    @Transactional
    public void deleteStoredFile(Long id) {
        Long userId = Long.valueOf(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());
        StoredFile storedFile = storedFileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found!"));
        if (userId != storedFile.getUserId()) {
            throw new RuntimeException("Current user is not authorized to delete this file");
        }
        storedFileRepository.delete(storedFile);
    }

}
