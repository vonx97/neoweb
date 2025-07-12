package com.neosoft.neoweb.services;

import com.neosoft.neoweb.configs.UpdateProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UpdateService {

    private final UpdateProperties properties;

    public UpdateService(UpdateProperties properties) {
        this.properties = properties;
    }

    public String getLatestVersion() throws IOException {
        return Files.readString(properties.getVersionFilePath()).trim();
    }

    public boolean versionIsLatest(String clientVersion) throws IOException {
        return getLatestVersion().equals(clientVersion);
    }

    public Path getUpdateZip(String version) {
        return properties.getFolderPath().resolve("client-" + version + ".zip");
    }

    public String calculateChecksum(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
        }
        byte[] digest = md.digest();
        return bytesToHex(digest);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
