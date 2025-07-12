package com.neosoft.neoweb.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UpdateProperties {

    @Value("${update.folder.path}")
    private String folderPath;

    @Value("${update.version.file}")
    private String versionFile;

    public Path getFolderPath() {
        return Paths.get(folderPath);
    }

    public Path getVersionFilePath() {
        return getFolderPath().resolve(versionFile);
    }
}