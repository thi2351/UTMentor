//package com.example.utmentor.presentation.controllers;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.HttpStatus;
//
//public abstract class BaseController {
//
//    protected <T> ResponseEntity<ApiResponse<T>> handle(GlobalResult<T> result) {
//        HttpStatus status = result.status() != null ? result.status() : HttpStatus.OK;
//
//        if (result.Errors() != null && !result.Errors().isEmpty()) {
//            return ResponseEntity
//                    .status(status)
//                    .body(ApiResponse.fail(result.Errors()));
//        }
//        return ResponseEntity
//                .status(status)
//                .body(ApiResponse.ok(result.data()));
//    }
//}
