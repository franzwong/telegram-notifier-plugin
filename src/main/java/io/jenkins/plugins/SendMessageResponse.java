package io.jenkins.plugins;

import com.fasterxml.jackson.annotation.JsonProperty;

// We only care the fields in error response
public class SendMessageResponse {

    @JsonProperty("ok")
    private boolean ok;

    // Only exists for error response
    @JsonProperty("error_code")
    private Integer errorCode;

    // Only exists for error response
    @JsonProperty("description")
    private String description;

    public boolean isOk() {
        return ok;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

}
