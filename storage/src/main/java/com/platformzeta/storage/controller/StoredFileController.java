package com.platformzeta.storage.controller;


import com.platformzeta.storage.dto.ErrorResponseDto;
import com.platformzeta.storage.dto.StoredFileDetailsDto;
import com.platformzeta.storage.dto.StoredFileDto;
import com.platformzeta.storage.service.IStoredFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Tag(
        name = "Storage Controller",
        description = "Storage Controller, its purpose is provide CRUD operation for user with jwt related token"
)
@RestController
@RequestMapping("/stored-file")
@RequiredArgsConstructor
public class StoredFileController {

    private final IStoredFileService storedFileService;

    @Operation(
            summary = "POST Store a file",
            description = "API for storing a file when providing JWT Token with correct login credentials"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createStoredFile(
            @RequestPart(value = "storedFileRequest") String storedFileRequestJson,
            @NonNull @RequestPart(value = "file") MultipartFile file
    ) {
        boolean isCreated = storedFileService.createStoredFile(storedFileRequestJson, file);
        if (isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("File successfully stored");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File storing failed!");
        }
    }

    @Operation(
            summary = "GET Stored file",
            description = "API for getting stored file for given user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = StoredFileDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getStoredFile(@PathVariable Long id) throws AccessDeniedException {
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

    @Operation(
            summary = "GET Stored file details",
            description = "API for getting stored file details for given user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = StoredFileDetailsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getStoredFileDetails(@PathVariable Long id) throws AccessDeniedException {
        Optional<?> storedFileDto = storedFileService.getStoredFile(id, false);
        if (storedFileDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(storedFileDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stored file not found!");
        }
    }

    @Operation(
            summary = "GET All stored file details",
            description = "API for getting all stored file details for given user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = StoredFileDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/details")
    public ResponseEntity<?> getStoredFileDetails() {
        Optional<List<StoredFileDetailsDto>> storedFileDto = storedFileService.getStoredFilesDetail();
        if (storedFileDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(storedFileDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stored file not found!");
        }
    }

    @Operation(
            summary = "PUT file",
            description = "API for modifying a stored file/details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
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

    @Operation(
            summary = "DELETE file",
            description = "API for deleting a stored file for a given user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "HTTP Status Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStoredFile(@PathVariable Long id) throws AccessDeniedException {
        storedFileService.deleteStoredFile(id);
        return ResponseEntity.status(HttpStatus.OK).body("Stored file deleted successfully!");
    }

}
