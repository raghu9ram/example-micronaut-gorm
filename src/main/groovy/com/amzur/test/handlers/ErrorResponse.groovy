package com.amzur.test.handlers




class ErrorResponse {
    int status
    String error
    String message

    ErrorResponse(int status, String error, String message) {
        this.status = status
        this.error = error
        this.message = message
    }
}
