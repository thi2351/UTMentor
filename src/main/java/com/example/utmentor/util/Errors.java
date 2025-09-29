package com.example.utmentor.util;

public final class Errors {
    private Errors() {}

    // UPPER_SNAKE_CASE for constants
    public static final ErrorItem USERNAME_EXISTS =
            new ErrorItem("username", "unresolved", "This request cannot be done now.");

    public static final ErrorItem STUDENTID_EXISTS =
            new ErrorItem("studentID", "existed", "Student ID already exists.");

    // Login errors
    public static final ErrorItem INVALID_CREDENTIALS =
            new ErrorItem("credentials", "invalid", "Username or password is incorrect.");

    // Registration errors
    public static final ErrorItem EMAIL_EXISTS =
            new ErrorItem("email", "existed", "Student email already exists.");
    
    public static final ErrorItem PASSWORD_MISMATCH =
            new ErrorItem("password", "mismatch", "Password and rewrite password do not match.");
    
    public static final ErrorItem INVALID_EMAIL_FORMAT =
            new ErrorItem("email", "invalid", "Invalid email format.");
    
    public static final ErrorItem WEAK_PASSWORD =
            new ErrorItem("password", "weak", "Password must be at least 8 characters long.");

    // Datacore errors
    public static final ErrorItem DATACORE_NOT_FOUND =
            new ErrorItem("datacore", "not_found", "Datacore record not found.");
    
    public static final ErrorItem DATACORE_EMAIL_EXISTS =
            new ErrorItem("email", "existed", "Student email already exists in datacore.");
    
    public static final ErrorItem DATACORE_ALREADY_DELETED =
            new ErrorItem("datacore", "already_deleted", "Datacore record is already deleted.");
}