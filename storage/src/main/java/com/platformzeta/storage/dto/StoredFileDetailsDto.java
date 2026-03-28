package com.platformzeta.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * Stored file details only
 * @param id
 * @param fileTitle
 * @param fileDescription
 * @param fileName
 * @param fileExtension
 */
public record StoredFileDetailsDto(
        @Schema(example = "1", description = "Stored file ID")
        Long id,
        @Schema(example = "My file", description = "Stored file title")
        String fileTitle,
        @Schema(example = "My file description", description = "Stored file description")
        String fileDescription,
        @Schema(example = "MyFile", description = "Stored file original name")
        String fileName,
        @Schema(example = "plain/text", description = "Stored file original extension")
        String fileExtension
) implements Serializable {}
