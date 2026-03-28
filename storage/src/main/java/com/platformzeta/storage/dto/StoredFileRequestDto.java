package com.platformzeta.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * File details for storage request
 * @param fileTitle
 * @param fileDescription
 */
public record StoredFileRequestDto(
        @Schema(example = "My file", description = "Stored file title")
        String fileTitle,
        @Schema(example = "My file description", description = "Stored file description")
        String fileDescription
) implements Serializable {}