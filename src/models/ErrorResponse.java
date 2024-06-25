package models;

public class ErrorResponse {
    public String error;

    public ErrorResponse(Exception exception) {
        error = exception.getMessage();
    }
}
