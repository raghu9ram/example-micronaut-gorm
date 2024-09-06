package com.amzur.test.handlers

class UserNotFound extends RuntimeException {

    UserNotFound(String msg) {
        super(msg)
    }
}
