package com.platformzeta.storage.dto;

import java.io.Serializable;

public record StoredFileDto(
        String fileTitle,
        String fileDescription,
        String fileName,
        String fileExtension,
        byte[] fileData
) implements Serializable {}
