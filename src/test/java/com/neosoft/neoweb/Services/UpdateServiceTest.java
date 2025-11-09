package com.neosoft.neoweb.Services;


import com.neosoft.neoweb.configs.UpdateProperties;
import com.neosoft.neoweb.services.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateServiceTest {

    @Mock
    private UpdateProperties properties;

    private UpdateService updateService;

    private Path versionFile;
    private Path zipFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        versionFile = Files.createTempFile("version", ".txt");
        Files.writeString(versionFile, "1.2.3");

        zipFile = Files.createTempFile("client-1.2.3", ".zip");

        when(properties.getVersionFilePath()).thenReturn(versionFile);
        when(properties.getFolderPath()).thenReturn(zipFile.getParent());

        updateService = new UpdateService(properties);
    }

    @Test
    void shouldReturnLatestVersion() throws IOException {
        String latestVersion = updateService.getLatestVersion();
        assertThat(latestVersion).isEqualTo("1.2.3");
    }

    @Test
    void shouldReturnTrue_whenClientVersionIsLatest() throws IOException {
        boolean result = updateService.versionIsLatest("1.2.3");
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenClientVersionIsOld() throws IOException {
        boolean result = updateService.versionIsLatest("1.0.0");
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnUpdateZipPath() {
        Path expected = zipFile.getParent().resolve("client-1.2.3.zip");
        Path result = updateService.getUpdateZip("1.2.3");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldCalculateChecksumCorrectly() throws IOException, NoSuchAlgorithmException {
        String checksum = updateService.calculateChecksum(versionFile);
        assertThat(checksum).isNotNull();
        assertThat(checksum).hasSize(64); // SHA-256 checksum uzunluğu 64 karakter
    }

    @Test
    void shouldThrowIOException_whenVersionFileDoesNotExist() {
        when(properties.getVersionFilePath()).thenReturn(Paths.get("nonexistent.txt"));

        assertThatThrownBy(() -> updateService.getLatestVersion())
                .isInstanceOf(IOException.class)
                .hasMessageContaining("nonexistent.txt");
    }

    @Test
    void shouldThrowIOException_whenChecksumFileDoesNotExist() {
        Path fakeFile = Paths.get("nonexistent.zip");

        assertThatThrownBy(() -> updateService.calculateChecksum(fakeFile))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("nonexistent.zip");
    }
}