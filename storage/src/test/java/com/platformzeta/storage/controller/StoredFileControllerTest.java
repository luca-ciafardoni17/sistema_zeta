package com.platformzeta.storage.controller;

import com.platformzeta.storage.config.security.JwtUtil;
import com.platformzeta.storage.service.IStoredFileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import com.platformzeta.storage.dto.StoredFileDto;
import com.platformzeta.storage.dto.StoredFileDetailsDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoredFileController.class)
@AutoConfigureMockMvc(addFilters = false)
class StoredFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IStoredFileService storedFileService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /stored-file - Successo")
    void createStoredFile_Success() throws Exception {
        String jsonRequest = "{\"fileTitle\":\"Test Title\", \"fileDescription\":\"Test Desc\"}";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        MockMultipartFile jsonPart = new MockMultipartFile("storedFileRequest", "", "application/json", jsonRequest.getBytes());
        when(storedFileService.createStoredFile(anyString(), any())).thenReturn(true);
        mockMvc.perform(multipart("/stored-file")
                        .file(file)
                        .file(jsonPart))
                .andExpect(status().isCreated())
                .andExpect(content().string("File successfully stored"));
    }

    @Test
    @DisplayName("POST /stored-file - Fallimento (500)")
    void createStoredFile_Failure() throws Exception {
        when(storedFileService.createStoredFile(anyString(), any())).thenReturn(false);
        mockMvc.perform(multipart("/stored-file")
                        .file(new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes()))
                        .file(new MockMultipartFile("storedFileRequest", "", "application/json", "{}".getBytes())))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("File storing failed!"));
    }

    @Test
    @DisplayName("GET /stored-file/{id} - Download binario")
    void getStoredFile_DownloadSuccess() throws Exception {
        byte[] content = "file content".getBytes();
        StoredFileDto dto = new StoredFileDto(1L, "Title", "Desc", "file.txt", "text/plain", content);
        doReturn(Optional.of(dto)).when(storedFileService).getStoredFile(eq(1L), eq(true));
        mockMvc.perform(get("/stored-file/1"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/plain"))
                .andExpect(content().bytes(content));
    }

    @Test
    @DisplayName("GET /stored-file/details - Lista completa")
    void getAllStoredFileDetails_Success() throws Exception {
        List<StoredFileDetailsDto> list = List.of(
                new StoredFileDetailsDto(1L, "F1", "D1", "n1", "txt"),
                new StoredFileDetailsDto(2L, "F2", "D2", "n2", "pdf")
        );
        when(storedFileService.getStoredFilesDetail()).thenReturn(Optional.of(list));
        mockMvc.perform(get("/stored-file/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fileTitle").value("F1"));
    }


    @Test
    @DisplayName("PUT /stored-file/{id} - Aggiornamento file")
    void updateStoredFile_Success() throws Exception {
        String jsonRequest = "{\"fileTitle\":\"Updated\"}";
        when(storedFileService.modifyStoredFile(anyLong(), anyString(), any())).thenReturn(true);
        mockMvc.perform(multipart("/stored-file/1")
                        .file(new MockMultipartFile("storedFileRequest", "", "application/json", jsonRequest.getBytes()))
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(content().string("Stored file updated successfully!"));
    }

    @Test
    @DisplayName("DELETE /stored-file/{id} - Eliminazione")
    void deleteStoredFile_Success() throws Exception {
        doNothing().when(storedFileService).deleteStoredFile(1L);
        mockMvc.perform(delete("/stored-file/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stored file deleted successfully!"));
        verify(storedFileService, times(1)).deleteStoredFile(1L);
    }

    @Test
    @DisplayName("GET /stored-file/{id} - Not Found")
    void getStoredFile_NotFound() throws Exception {
        when(storedFileService.getStoredFile(anyLong(), anyBoolean())).thenReturn(Optional.empty());
        mockMvc.perform(get("/stored-file/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Stored file not found!"));
    }
}