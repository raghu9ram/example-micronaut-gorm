package com.amzur.test.controller

import com.amzur.test.model.TransactionModel
import com.amzur.test.service.KafkaProducerClient
import com.amzur.test.service.TransactionService
import groovy.transform.ToString
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.http.MediaType

import javax.inject.Inject

@Controller("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class TransactionController {

    @Inject
    TransactionService transactionService

    @Inject
    KafkaProducerClient kafkaProducerClient

    @Post("/")
    HttpResponse<TransactionModel> createATransaction(@Body TransactionModel transactionModel) {
        try {
            def transaction = transactionService.createATransaction(transactionModel)
            return HttpResponse.created(transaction)
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
            return HttpResponse.serverError("Error retrieving transaction: ${e.message}")
        }
    }

    @Post("/transfer")
    HttpResponse<TransactionModel> performTransfer(@Body TransferRequest transferRequest) {
        try {
            def transaction = transactionService.performTransfer(
                    transferRequest.fromUserId,
                    transferRequest.fromBankId,
                    transferRequest.toUserId,
                    transferRequest.amount,
                    transferRequest.upiPin
            )
            String message = """{
            "transaction Id": "${transaction.id}",
            "recipient": "${transaction.recipientMobile}",
            "amount": "${transaction.amount}",
             "date":"${transaction.dateTime}"
        }"""
            kafkaProducerClient.sendMessage("payment-topic", message)
            return HttpResponse.created(transaction)
        } catch (IllegalArgumentException e) {
            return HttpResponse.badRequest([message: e.message])
        } catch (SecurityException e) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED).body([message: e.message])
        } catch (IllegalStateException e) {
            return HttpResponse.badRequest([message: e.message])
        } catch (Exception e) {
            return HttpResponse.serverError([message: e.message])
        }
    }

}

// TransferRequest model class for type safety
@ToString
class TransferRequest {
    Long fromUserId
    Long fromBankId
    Long toUserId
    BigDecimal amount
    String upiPin
}
