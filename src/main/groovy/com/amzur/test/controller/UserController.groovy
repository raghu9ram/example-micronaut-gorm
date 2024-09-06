package com.amzur.test.controller

import com.amzur.test.handlers.UserNotFound
import com.amzur.test.model.BankModel
import com.amzur.test.model.UserModel
import com.amzur.test.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put

import javax.inject.Inject

@Controller("/users")
class UserController {
    @Inject
    UserService userService
    UserController(UserService userService) {
        this.userService = userService
    }

    @Post("/")
    def createAUser(@Body UserModel userModel) {
        try {
            def user = userService.createAUser(userModel)
            if(user) {
                return HttpResponse.created(user)
            } else {
                return HttpResponse.badRequest("Failed to add user")
            }
        } catch (Exception e) {
            return HttpResponse.serverError("Error creating user: ${e.message}")
        }
    }

    @Post("/login")
    def login(@Body UserModel userModel) {
        try {
            def user = userService.login(userModel.mobileNumber, userModel.userPin)
            if(user) {
                return HttpResponse.ok(user)
            } else {
                return HttpResponse.badRequest("Failed to recognise user details")
            }
        }  catch (UserNotFound e) {
            return HttpResponse.badRequest("Invalid credentials: ${e.message}")
        }  catch (Exception e) {
            return HttpResponse.serverError("An Error occured: ${e.message}")
        }
    }

    @Get("/{id}")
    HttpResponse<UserModel> getAUser(@PathVariable Long id) {
        try {
            UserModel user = userService.getAUser(id)
            return HttpResponse.ok(user)
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("User with ID $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving user: ${e.message}")
        }
    }

    @Put("/{id}")
    def updateAUser(@PathVariable Long id, @Body UserModel userModel) {
        try {
            userService.updateAUser(id, userModel)
            return HttpResponse.ok()
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("User with Id $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving user: ${e.message}")
        }
    }

    @Get("/{id}/userBanks")
    HttpResponse<List<BankModel>> getUserBanks(@PathVariable Long id) {
        try {
            List<BankModel> banks = userService.getUserBanks(id)
            return HttpResponse.ok(banks)
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("User with ID $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving user banks: ${e.message}")
        }
    }
}
