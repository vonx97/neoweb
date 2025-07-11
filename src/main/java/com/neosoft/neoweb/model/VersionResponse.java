package com.neosoft.neoweb.model;

public class VersionResponse {

    private boolean upToDate;
    private String latestVersion;
    private String downloadPath;   // "client-1.3.0.zip" gibi
    private String releaseNotes;   // (opsiyonel)
    private String checksum;       // (opsiyonel)

    public VersionResponse() {}

    public VersionResponse(boolean upToDate, String latestVersion, String downloadPath, String releaseNotes, String checksum) {
        this.upToDate = upToDate;
        this.latestVersion = latestVersion;
        this.downloadPath = downloadPath;
        this.releaseNotes = releaseNotes;
        this.checksum = checksum;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
