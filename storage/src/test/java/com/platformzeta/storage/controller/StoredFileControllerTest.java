package com.platformzeta.storage.controller;

import com.platformzeta.storage.config.security.JwtUtil;
import com.platformzeta.storage.dto.StoredFileDetailsDto;
import com.platformzeta.storage.dto.StoredFileDto;
import com.platformzeta.storage.service.IStoredFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StoredFileController.class)
@AutoConfigureMockMvc(addFilters = false)
class StoredFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IStoredFileService storedFileService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void createStoredFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Contenuto del file".getBytes());
        MockMultipartFile jsonPart = new MockMultipartFile("storedFileRequest", "", MediaType.APPLICATION_JSON_VALUE, "{\"fileTitle\":\"Test Title\"}".getBytes());
        when(storedFileService.createStoredFile(anyString(), any())).thenReturn(true);
        mockMvc.perform(multipart("/stored-file")
                        .file(file)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().string("File successfully stored"));
    }

    @Test
    void createStoredFile_Failure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Contenuto del file".getBytes());
        MockMultipartFile jsonPart = new MockMultipartFile("storedFileRequest", "", MediaType.APPLICATION_JSON_VALUE, "{\"fileTitle\":\"Test Title\"}".getBytes());
        when(storedFileService.createStoredFile(anyString(), any())).thenReturn(false);
        mockMvc.perform(multipart("/stored-file")
                        .file(file)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("File storing failed!"));
    }

    @Test
    void getStoredFile_Success() throws Exception {
        byte[] fileData = "File content".getBytes();
        StoredFileDto mockDto = new StoredFileDto(
                "Mock file title",
                "Mock file description",
                "mockFile.txt",
                "text/plain",
                fileData
        );
        when((Optional) storedFileService.getStoredFile(anyLong(), eq(true))).thenReturn(Optional.of(mockDto));
        mockMvc.perform(get("/stored-file/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(header().longValue("Content-Length", fileData.length))
                .andExpect(content().bytes(fileData));
    }

    @Test
    void getStoredFile_NotFound() throws Exception {
        when((Optional) storedFileService.getStoredFile(anyLong(), eq(true))).thenReturn(Optional.empty());
        mockMvc.perform(get("/stored-file/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Stored file not found!"));
    }

    @Test
    void getStoredFileDetails_Success() throws Exception {
        StoredFileDetailsDto mockDetailsDto = new StoredFileDetailsDto(
                1L,
                "Mock file title",
                "Mock file description",
                "mockFile.txt",
                "text/plain"
        );
        when((Optional) storedFileService.getStoredFile(anyLong(), eq(false))).thenReturn(Optional.of(mockDetailsDto));
        mockMvc.perform(get("/stored-file/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileTitle").value("Mock file title"));
    }

    @Test
    void getStoredFilesDetailList_Success() throws Exception {
        StoredFileDetailsDto mockDetailsDto = new StoredFileDetailsDto(
                1L,
                "Mock file title",
                "Mock file description",
                "mockFile.txt",
                "text/plain"
        );
        List<StoredFileDetailsDto> list = Collections.singletonList(mockDetailsDto);
        when(storedFileService.getStoredFilesDetail()).thenReturn(Optional.of(list));
        mockMvc.perform(get("/stored-file/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fileTitle").value("Mock file title"));
    }

    @Test
    void updateStoredFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test_updated.txt", MediaType.TEXT_PLAIN_VALUE, "Updated".getBytes());
        MockMultipartFile jsonPart = new MockMultipartFile("storedFileRequest", "", MediaType.APPLICATION_JSON_VALUE, "{\"fileTitle\":\"Updated Title\"}".getBytes());
        when(storedFileService.modifyStoredFile(anyLong(), anyString(), any())).thenReturn(true);
        mockMvc.perform(multipart(HttpMethod.PUT, "/stored-file/1")
                        .file(file)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("Stored file updated successfully!"));
    }

    @Test
    void deleteStoredFile_Success() throws Exception {
        doNothing().when(storedFileService).deleteStoredFile(anyLong());
        mockMvc.perform(delete("/stored-file/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stored file deleted successfully!"));
    }
}