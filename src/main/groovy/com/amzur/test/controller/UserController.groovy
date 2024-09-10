package com.amzur.test.controller

import com.amzur.test.handlers.UserNotFound
import com.amzur.test.model.BankModel
import com.amzur.test.model.TransactionModel
import com.amzur.test.model.UserModel
import com.amzur.test.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Produces
import io.micronaut.http.MediaType

import javax.inject.Inject

@Controller("/users")
@Produces(MediaType.APPLICATION_JSON)
class UserController {

    @Inject
    UserService userService

    @Post("/")
    HttpResponse<UserModel> createAUser(@Body UserModel userModel) {
        try {
            def user = userService.createAUser(userModel)
            return HttpResponse.created(user)
        } catch (Exception e) {
            return HttpResponse.serverError("Error creating user: ${e.message}")
        }
    }

    @Post("/login")
    HttpResponse<UserModel> login(@Body UserModel userModel) {
        try {
            def user = userService.login(userModel.mobileNumber, userModel.userPin)
            return HttpResponse.ok(user)
        } catch (UserNotFound e) {
            return HttpResponse.badRequest("Invalid credentials: ${e.message}")
        } catch (Exception e) {
            return HttpResponse.serverError("An error occurred: ${e.message}")
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
    HttpResponse<Void> updateAUser(@PathVariable Long id, @Body UserModel userModel) {
        try {
            userService.updateAUser(id, userModel)
            return HttpResponse.ok()
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("User with ID $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error updating user: ${e.message}")
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

    @Get("/{id}/transactions")
    HttpResponse<List<TransactionModel>> getUserTransactions(@PathVariable Long id) {
        try {
            List<TransactionModel> transactions = userService.getUserTransactions(id)
            return HttpResponse.ok(transactions)
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("User with ID $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving user transactions: ${e.message}")
        }
    }

    @Get("/byMobileNumber/{mobileNumber}")
    HttpResponse<UserModel> getUserByMobileNumber(@PathVariable String mobileNumber) {
        try {
            UserModel user = userService.getUserByMobileNumber(mobileNumber)
            return HttpResponse.ok(user)
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("User with mobile number $mobileNumber not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving user: ${e.message}")
        }
    }
}
