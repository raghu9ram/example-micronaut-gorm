package com.amzur.test.controller

import com.amzur.test.model.TransactionModel
import com.amzur.test.service.TransactionService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

import javax.inject.Inject

@Controller("/transactions")
class TransactionController {
    @Inject
    TransactionService transactionService
    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService
    }

    @Post("/")
    def createATransaction(@Body TransactionModel transactionModel) {
        try {
            def transaction = transactionService.createATransaction(transactionModel)
            if(transaction) {
                return HttpResponse.created(transaction)
            } else {
                return HttpResponse.badRequest("Failed to add the transaction")
            }
        } catch (RuntimeException e) {
            return HttpResponse.badRequest("Validation or persistence error: ${e.message}")
        } catch (Exception e) {
            return HttpResponse.serverError("Error creating the transaction: ${e.message}")
        }
    }

    @Get("/")
    HttpResponse<List<TransactionModel>> getAllTransactions() {
        try {
            List<TransactionModel> transactionModels = transactionService.getAllTransactions()
            return HttpResponse.ok(transactionModels)
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving transactions: ${e.message}")
        }
    }

    @Get("/{id}")
    HttpResponse<TransactionModel> getATransaction(@PathVariable Long id) {
        try {
            TransactionModel transaction = transactionService.getATransaction(id)
            return HttpResponse.ok(transaction)
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("Transaction with ID $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving event: ${e.message}")
        }
    }
}
