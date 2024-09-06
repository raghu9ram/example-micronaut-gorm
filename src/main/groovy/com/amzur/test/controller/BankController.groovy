package com.amzur.test.controller

import com.amzur.test.model.BankModel
import com.amzur.test.service.BankService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

import javax.inject.Inject

@Controller("/banks")
class BankController {
    @Inject
    BankService bankService
    BankController(BankService bankService) {
        this.bankService = bankService
    }

    @Post("/")
    HttpResponse<?> createABank(@Body BankModel bankModel) {
        try {
            def bank = bankService.createABank(bankModel)
            return HttpResponse.created(bank)
        } catch (IllegalArgumentException e) {
            return HttpResponse.badRequest("Invalid request: ${e.message}")
        } catch (RuntimeException e) {
            return HttpResponse.badRequest("Validation or persistence error: ${e.message}")
        } catch (Exception e) {
            return HttpResponse.serverError("Error creating bank account: ${e.message}")
        }
    }

    @Get("/predefined")
    HttpResponse<List<Map>> getPredefinedBanks() {
        try {
            List<BankModel> predefinedBanks = bankService.getPredefinedBanks()
            List<Map> banksList = predefinedBanks.collect { [ id: it.id, bankName: it.bankName ] }
            return HttpResponse.ok(banksList)
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving predefined banks: ${e.message}")
        }
    }


    @Get("/")
    HttpResponse<List<BankModel>> getAllBanks() {
        try {
            List<BankModel> banks = bankService.getAllBanks()
            return HttpResponse.ok(banks)
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving bank accounts: ${e.message}")
        }
    }

    @Get("/{id}")
    HttpResponse<BankModel> getABank(@PathVariable Long id) {
        try {
            BankModel bank = bankService.getABank(id)
            return HttpResponse.ok(bank)
        } catch (NoSuchElementException e) {
            return HttpResponse.notFound("Bank with ID $id not found")
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving bank account: ${e.message}")
        }
    }
}
