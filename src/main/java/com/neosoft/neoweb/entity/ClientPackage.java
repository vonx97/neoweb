package com.neosoft.neoweb.entity;

import enums.PackageStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
public class ClientPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String version;

    @Column(name="release_notes")
    private String releaseNotes;

    @Column(name="check_sum",nullable = false)
    private String checkSum;

    @Column(name = "upload_date",nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING) // Enum değerlerini DB’de string olarak saklar
    @Column(nullable = false)
    private PackageStatus status = PackageStatus.DRAFT;

    @Column(name="file_size")
    private Long  fileSize;

    @Column(name="mandatory_update")
    private boolean mandatoryUpdate = false;

    @Column(nullable = true)
    private String uploader;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isMandatoryUpdate() {
        return mandatoryUpdate;
    }

    public void setMandatoryUpdate(boolean mandatoryUpdate) {
        this.mandatoryUpdate = mandatoryUpdate;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}
