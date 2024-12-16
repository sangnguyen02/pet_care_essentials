package com.example.uiux.Utils;

public class ResponseParts {
    private String suppliesId;
    private String description;

    public ResponseParts(String suppliesId, String description) {
        this.suppliesId = suppliesId;
        this.description = description;
    }

    public String getSuppliesId() {
        return suppliesId;
    }

    public String getDescription() {
        return description;
    }
}

