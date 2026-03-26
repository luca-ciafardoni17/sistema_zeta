package com.platformzeta.storage.repository;

import com.platformzeta.storage.entity.StoredFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StoredFileRepositoryTest {

    @Autowired
    private StoredFileRepository repository;

    private Long existingFileId;
    private final String OWNER_EMAIL = "mario.rossi@aruba.it";

    @BeforeEach
    void setUp() {
        StoredFile file = new StoredFile();
        file.setFileTitle("Titolo Originale");
        file.setFileDescription("Descrizione Originale");
        file.setEmail(OWNER_EMAIL);
        file.setFileName("old.txt");
        file.setFileExtension("text/plain");
        file.setFileData("vecchio contenuto".getBytes());
        file.setCreatedAt(Instant.now());
        file.setCreatedBy(OWNER_EMAIL);

        StoredFile saved = repository.save(file);
        existingFileId = saved.getId();
    }

    @Test
    void findByEmail_ShouldReturnFiles() {
        List<StoredFile> results = repository.findByEmail(OWNER_EMAIL);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getEmail()).isEqualTo(OWNER_EMAIL);
    }

    @Test
    void updateStoredFile_ShouldUpdateAllFields() {
        byte[] newData = "nuovo contenuto".getBytes();
        Instant now = Instant.now();

        int result = repository.updateStoredFile(
                existingFileId,
                "Nuovo Titolo",
                "Nuova Desc",
                "new.pdf",
                "application/pdf",
                newData,
                now,
                OWNER_EMAIL
        );

        assertThat(result).isEqualTo(1); // Una riga aggiornata

        // Verifichiamo che i dati siano effettivamente cambiati nel DB
        StoredFile updated = repository.findById(existingFileId).orElseThrow();
        assertThat(updated.getFileTitle()).isEqualTo("Nuovo Titolo");
        assertThat(updated.getFileData()).isEqualTo(newData);
        assertThat(updated.getFileName()).isEqualTo("new.pdf");
    }

    @Test
    void updateStoredFileDetails_ShouldUpdateOnlyMetadata() {
        int result = repository.updateStoredFileDetails(
                existingFileId,
                "Titolo Solo Dettagli",
                "Desc Solo Dettagli",
                Instant.now(),
                OWNER_EMAIL
        );

        assertThat(result).isEqualTo(1);

        StoredFile updated = repository.findById(existingFileId).orElseThrow();
        assertThat(updated.getFileTitle()).isEqualTo("Titolo Solo Dettagli");
        // Il file name NON deve essere cambiato (era "old.txt" nel setUp)
        assertThat(updated.getFileName()).isEqualTo("old.txt");
    }

    @Test
    void update_ShouldReturnZero_IfIdDoesNotExist() {
        int result = repository.updateStoredFileDetails(
                999L,
                "Titolo",
                "Desc",
                Instant.now(),
                OWNER_EMAIL
        );

        assertThat(result).isEqualTo(0);
    }
}