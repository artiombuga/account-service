package account.dto;

import java.time.LocalDateTime;

public record CustomErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
    @Override
    public LocalDateTime timestamp() {
        return timestamp;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String error() {
        return error;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String path() {
        return path;
    }
}
