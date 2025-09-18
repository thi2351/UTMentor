package com.example.utmentor.util;

import com.example.utmentor.util.ErrorItem;

public final class Errors {
    private Errors() {}

    // UPPER_SNAKE_CASE for constants
    public static final ErrorItem USERNAME_EXISTS =
            new ErrorItem("username", "unresolved", "This request cannot be done now.");

    public static final ErrorItem STUDENTID_EXISTS =
            new ErrorItem("studentID", "existed", "Student ID already exists.");


}