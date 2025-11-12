package com.gsw.service_log.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAnexo {
    PDF("application/pdf"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    MP4("video/mp4"),
    JPEG("image/jpeg"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String mimeType;

    TipoAnexo(String mimeType) {
        this.mimeType = mimeType;
    }

    @JsonValue
    public String getMimeType() {
        return mimeType;
    }
}
