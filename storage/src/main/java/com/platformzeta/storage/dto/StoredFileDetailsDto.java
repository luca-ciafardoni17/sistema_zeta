package com.platformzeta.storage.dto;

import java.io.Serializable;

public record StoredFileDetailsDto(
        Long id,
        String fileTitle,
        String fileDescription,
        String fileName,
        String fileExtension
) implements Serializable {}
