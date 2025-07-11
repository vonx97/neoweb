package com.neosoft.neoweb.model;

public class VersionRequest {

    private String clientId;
    private String version;

    public VersionRequest(String clientId, String version) {
        this.clientId = clientId;
        this.version = version;
    }

    public VersionRequest() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
