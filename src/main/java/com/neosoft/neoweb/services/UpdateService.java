package com.neosoft.neoweb.services;

import com.neosoft.neoweb.configs.UpdateProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
public class UpdateService {

    private final UpdateProperties properties;

    public UpdateService(UpdateProperties properties) {
        this.properties = properties;
    }

    /**
     * Dosyadaki en son versiyonu alır.
     */
    public String getLatestVersion() throws IOException {
        Path versionFile = properties.getVersionFilePath();
        if (!Files.exists(versionFile)) {
            throw new IOException("Version file not found: " + versionFile);
        }
        return Files.readString(versionFile).trim();
    }

    /**
     * Verilen client sürümünün en güncel sürüm olup olmadığını kontrol eder.
     */
    public boolean versionIsLatest(String clientVersion) throws IOException {
        return clientVersion != null && Objects.equals(clientVersion, getLatestVersion());
    }

    /**
     * Belirtilen sürüme ait update zip dosyasının yolunu döner.
     */
    public Path getUpdateZip(String version) {
        return properties.getFolderPath().resolve("client-" + version + ".zip");
    }

    /**
     * Verilen dosyanın SHA-256 checksum'unu hesaplar.
     */
    public String calculateChecksum(Path file) throws IOException, NoSuchAlgorithmException {
        if (!Files.exists(file)) {
            throw new IOException("File not found: " + file);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
        }
        return bytesToHex(md.digest());
    }

    /**
     * Byte array'ini hex string'e çevirir.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}