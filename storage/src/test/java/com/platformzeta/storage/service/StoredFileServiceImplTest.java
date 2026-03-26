package com.platformzeta.storage.service;

import com.platformzeta.storage.entity.StoredFile;
import com.platformzeta.storage.repository.StoredFileRepository;
import com.platformzeta.storage.service.impl.StoredFileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoredFileServiceImplTest {

    @Mock
    private StoredFileRepository storedFileRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StoredFileServiceImpl storedFileService;

    private final String VALID_EMAIL = "mario.rossi@aruba.it";
    private final String INVALID_EMAIL = "hacker.cattivissimo@gmail.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockUserEmail(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
    }

    @Test
    void createStoredFile_Success_WithArubaEmail() throws IOException {
        mockUserEmail(VALID_EMAIL);
        String jsonRequest = "{\"fileTitle\":\"Test\", \"fileDescription\":\"Desc\"}";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "data".getBytes()
        );
        boolean result = storedFileService.createStoredFile(jsonRequest, file);
        assertTrue(result);
        verify(storedFileRepository, times(1)).save(any(StoredFile.class));
    }

    @Test
    void createStoredFile_Failure_WithWrongDomain() {
        mockUserEmail(INVALID_EMAIL);
        String jsonRequest = "{\"fileTitle\":\"Test\", \"fileDescription\":\"Desc\"}";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "data".getBytes()
        );
        boolean result = storedFileService.createStoredFile(jsonRequest, file);
        assertFalse(result);
        verify(storedFileRepository, never()).save(any());
    }

    @Test
    void getStoredFile_ThrowsException_WhenUserNotOwner() {
        mockUserEmail(VALID_EMAIL);
        StoredFile fileInDb = new StoredFile();
        fileInDb.setEmail("non.mario.rossi@aruba.it");
        when(storedFileRepository.findById(1L)).thenReturn(Optional.of(fileInDb));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storedFileService.getStoredFile(1L, true);
        });
        assertEquals("Current user is not authorized to download requested file!", exception.getMessage());
    }

    @Test
    void modifyStoredFile_Success_DetailsOnly() {
        mockUserEmail(VALID_EMAIL);
        StoredFile fileInDb = new StoredFile();
        fileInDb.setEmail(VALID_EMAIL);
        when(storedFileRepository.findById(1L)).thenReturn(Optional.of(fileInDb));
        when(storedFileRepository.updateStoredFileDetails(
                anyLong(),
                anyString(),
                anyString(),
                any(),
                anyString())
        ).thenReturn(1);
        String jsonRequest = "{\"fileTitle\":\"Updated\", \"fileDescription\":\"New Desc\"}";
        boolean result = storedFileService.modifyStoredFile(1L, jsonRequest, null);
        assertTrue(result);
        verify(storedFileRepository).updateStoredFileDetails(
                eq(1L),
                eq("Updated"),
                eq("New Desc"),
                any(),
                eq(VALID_EMAIL)
        );
    }

    @Test
    void deleteStoredFile_Success() {
        mockUserEmail(VALID_EMAIL);
        StoredFile fileInDb = new StoredFile();
        fileInDb.setEmail(VALID_EMAIL);
        when(storedFileRepository.findById(1L)).thenReturn(Optional.of(fileInDb));
        assertDoesNotThrow(() -> storedFileService.deleteStoredFile(1L));
        verify(storedFileRepository).delete(fileInDb);
    }

    @Test
    void deleteStoredFile_ThrowsException_WhenFileNotFound() {
        mockUserEmail(VALID_EMAIL);
        when(storedFileRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> storedFileService.deleteStoredFile(1L));
    }
}