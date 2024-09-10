package com.amzur.test.controller

import com.amzur.test.model.BankModel
import com.amzur.test.service.BankService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.MediaType

import javax.inject.Inject

@Controller("/banks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class BankController {

    @Inject
    BankService bankService

    @Post("/")
    HttpResponse<BankModel> createABank(@Body BankModel bankModel) {
        try {
            def bank = bankService.createABank(bankModel)
            if (bank) {
                return HttpResponse.created(bank)
            } else {
                return HttpResponse.badRequest("Failed to add bank account")
            }
        } catch (RuntimeException e) {
            return HttpResponse.badRequest("Validation or persistence error: ${e.message}")
        } catch (Exception e) {
            return HttpResponse.serverError("Error creating bank account: ${e.message}")
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
            if (bank) {
                return HttpResponse.ok(bank)
            } else {
                return HttpResponse.notFound("Bank with ID $id not found")
            }
        } catch (Exception e) {
            return HttpResponse.serverError("Error retrieving bank account: ${e.message}")
        }
    }

    @Patch("/primary/{userId}")
    HttpResponse<?> updatePrimaryBank(@PathVariable Long userId, @Body Map<String, Boolean> body) {
        boolean primaryBank = body.primaryBank
        if (primaryBank) {
            bankService.updatePrimaryBank(userId, primaryBank)
        }
        return HttpResponse.noContent()
    }
}
