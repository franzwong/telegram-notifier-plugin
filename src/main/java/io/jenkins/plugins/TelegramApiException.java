package io.jenkins.plugins;

public class TelegramApiException extends Exception {

    private final int errorCode;

    private final String description;

    public TelegramApiException(int errorCode, String description) {
        super();
        this.errorCode = errorCode;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Error code: %d, Description: %s", errorCode, description);
    }

}
