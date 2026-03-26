package com.platformzeta.storage.dto;

import java.io.Serializable;

public record StoredFileRequestDto(
        String fileTitle,
        String fileDescription
) implements Serializable {}