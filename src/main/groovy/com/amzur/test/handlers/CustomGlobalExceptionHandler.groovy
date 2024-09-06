package com.amzur.test.handlers


import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error



@Controller
class CustomGlobalExceptionHandler {




    @Error(global = true, exception = UserNotFound.class)
    HttpResponse<ErrorResponse> handleLoginUser(UserNotFound ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.code, "Not Found", ex.message)
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(errorResponse) as HttpResponse<ErrorResponse>
    }




}