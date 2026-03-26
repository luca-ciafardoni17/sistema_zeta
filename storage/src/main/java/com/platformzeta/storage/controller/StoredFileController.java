package com.platformzeta.storage.controller;


import com.platformzeta.storage.dto.StoredFileDetailsDto;
import com.platformzeta.storage.dto.StoredFileDto;
import com.platformzeta.storage.dto.StoredFileRequestDto;
import com.platformzeta.storage.entity.StoredFile;
import com.platformzeta.storage.service.IStoredFileService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stored-file")
@RequiredArgsConstructor
public class StoredFileController {

    private final IStoredFileService storedFileService;

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createStoredFile(
            @RequestPart(value = "storedFileRequest") String storedFileRequestJson,
            @RequestPart(value = "file") MultipartFile file
    ) {
        boolean isCreated = storedFileService.createStoredFile(storedFileRequestJson, file);
        if (isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("File successfully stored");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File storing failed!");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoredFile(@PathVariable Long id) {
        Optional<StoredFileDto> storedFileDto = (Optional<StoredFileDto>) storedFileService.getStoredFile(id, true);
        if (storedFileDto.isPresent()) {
            byte[] file = storedFileDto.get().fileData();
            if (file == null || file.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stored file not found!");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(storedFileDto.get().fileExtension()));
            headers.setContentLength(file.length);
            return new ResponseEntity<>(file, headers, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stored file not found!");
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getStoredFileDetails(@PathVariable Long id) {
        Optional<?> storedFileDto = storedFileService.getStoredFile(id, false);
        if (storedFileDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(storedFileDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stored file not found!");
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getStoredFileDetails() {
        Optional<List<StoredFileDetailsDto>> storedFileDto = storedFileService.getStoredFilesDetail();
        if (storedFileDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(storedFileDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stored file not found!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStoredFile(
            @PathVariable Long id,
            @RequestPart(value = "storedFileRequest") String storedFileRequestJson,
            @Nullable @RequestPart(value = "file") MultipartFile file
    ) throws IOException {
        boolean isModified = storedFileService.modifyStoredFile(id, storedFileRequestJson, file);
        if (isModified) {
            return ResponseEntity.status(HttpStatus.OK).body("Stored file updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stored file update failed!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStoredFile(@PathVariable Long id) {
        storedFileService.deleteStoredFile(id);
        return ResponseEntity.status(HttpStatus.OK).body("Stored file deleted successfully!");
    }

}
