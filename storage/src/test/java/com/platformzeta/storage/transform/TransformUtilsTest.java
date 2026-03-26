package com.platformzeta.storage.transform;

import com.platformzeta.storage.dto.StoredFileDetailsDto;
import com.platformzeta.storage.dto.StoredFileDto;
import com.platformzeta.storage.dto.StoredFileRequestDto;
import com.platformzeta.storage.entity.StoredFile;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TransformUtilsTest {

    @Test
    void transformDtoToEntity_WithValidFile_ShouldMapEverything() throws IOException {
        StoredFileRequestDto requestDto = new StoredFileRequestDto(
                "Il mio titolo",
                "Una descrizione"
        );
        byte[] content = "Hello World".getBytes();
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content
        );
        String email = "mario.rossi@aruba.it";
        StoredFile result = TransformUtils.transformDtoToEntity(requestDto, mockFile, email);
        assertNotNull(result);
        assertEquals("Il mio titolo", result.getFileTitle());
        assertEquals("Una descrizione", result.getFileDescription());
        assertEquals("test.txt", result.getFileName());
        assertEquals("text/plain", result.getFileExtension());
        assertArrayEquals(content, result.getFileData());
        assertEquals(email, result.getEmail());
        assertEquals(email, result.getCreatedBy());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void transformDtoToEntity_WithNullFile_ShouldNotSetFileData() {
        StoredFileRequestDto requestDto = new StoredFileRequestDto("Titolo", "Desc");
        String email = "mario.rossi@aruba.it";
        StoredFile result = TransformUtils.transformDtoToEntity(requestDto, null, email);
        assertNull(result.getFileData());
        assertNull(result.getFileName());
        assertEquals("Titolo", result.getFileTitle());
    }

    @Test
    void transformEntityToDto_ShouldMapCorrectly() {
        StoredFile entity = new StoredFile();
        entity.setFileTitle("Titolo");
        entity.setFileDescription("Desc");
        entity.setFileName("file.pdf");
        entity.setFileExtension("application/pdf");
        entity.setFileData(new byte[]{1, 2, 3});
        StoredFileDto result = TransformUtils.transformEntityToDto(entity);
        assertEquals(entity.getFileTitle(), result.fileTitle());
        assertEquals(entity.getFileName(), result.fileName());
        assertArrayEquals(entity.getFileData(), result.fileData());
    }

    @Test
    void transformEntityToDtoDetails_ShouldMapCorrectlyWithoutBinaryData() {
        StoredFile entity = new StoredFile();
        entity.setId(100L);
        entity.setFileTitle("Titolo");
        entity.setFileDescription("Desc");
        entity.setFileName("image.png");
        entity.setFileExtension("image/png");
        StoredFileDetailsDto result = TransformUtils.transformEntityToDtoDetails(entity);
        assertEquals(100L, result.id());
        assertEquals("Titolo", result.fileTitle());
        assertEquals("image.png", result.fileName());
    }
}