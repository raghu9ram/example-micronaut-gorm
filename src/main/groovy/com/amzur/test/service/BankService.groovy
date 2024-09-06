package com.amzur.test.service

import com.amzur.test.domain.BankDomain
import com.amzur.test.domain.UserDomain
import com.amzur.test.model.BankModel
import com.amzur.test.model.TransactionModel
import com.amzur.test.model.UserModel
import grails.gorm.transactions.Transactional
import javax.inject.Singleton

@Singleton
class BankService {

    @Transactional
    def createABank(BankModel bankModel) {
        Long userDomainId = bankModel.userModel?.id

        if (!userDomainId) {
            throw new IllegalArgumentException("UserDomain ID is required")
        }

        UserDomain userDomain = UserDomain.findById(userDomainId)

        if (!userDomain) {
            throw new NoSuchElementException("UserDomain with ID $userDomainId not found")
        }

        // Check if the bank name is from the predefined list
        def predefinedBank = BankDomain.findByBankName(bankModel.bankName)

        if (!predefinedBank) {
            throw new IllegalArgumentException("Bank name ${bankModel.bankName} is not valid")
        }

        // Check for existing bank record with the same account number
        def existingBank = BankDomain.findByAccountNumber(bankModel.accountNumber)
        if (existingBank) {
            throw new IllegalArgumentException("Bank with account number ${bankModel.accountNumber} already exists")
        }

        BankDomain bankDomain = toBankDomain(bankModel, userDomain)

        if (!bankDomain.validate()) {
            def errors = bankDomain.errors.allErrors.collect { error ->
                "${error.field}: ${error.defaultMessage}"
            }
            throw new RuntimeException("Validation errors: ${errors.join(', ')}")
        }

        if (!bankDomain.save(flush: true)) {
            def errors = bankDomain.errors.allErrors.collect { error ->
                "${error.field}: ${error.defaultMessage}"
            }
            throw new RuntimeException("Failed to save BankDomain: ${errors.join(', ')}")
        }

        return toBankModel(bankDomain)
    }

    @Transactional
    List<BankModel> getPredefinedBanks() {
        // Fetch only the bank names and ids
        List<BankDomain> predefinedBanks = BankDomain.findAllByBankNameInList(["SBI", "HDFC", "ICICI"]) // Example filter, adjust as needed
        return predefinedBanks.collect { toBankModel(it) }
    }


    @Transactional
    List<BankModel> getAllBanks() {
        List<BankDomain> bankDomains = BankDomain.list()
        bankDomains.collect { toBankModel(it) }
    }

    @Transactional
    BankModel getABank(Long id) {
        BankDomain bankDomain = BankDomain.findById(id)
        if (bankDomain) {
            return toBankModel(bankDomain)
        } else {
            throw new NoSuchElementException("Bank with ID $id not found")
        }
    }

    static BankModel toBankModel(BankDomain bankDomain) {
        new BankModel(
                id: bankDomain.id,
                bankName: bankDomain.bankName,
                accountNumber: bankDomain.accountNumber,
                bankPin: bankDomain.bankPin,
                transactionLimit: bankDomain.transactionLimit,
                balance: bankDomain.balance,
                userModel: new UserModel(
                        id: bankDomain.userDomain.id,
                        mobileNumber: bankDomain.userDomain.mobileNumber,
                        userPin: bankDomain.userDomain.userPin,
                        firstName: bankDomain.userDomain.firstName,
                        lastName: bankDomain.userDomain.lastName,
                        email: bankDomain.userDomain.email
                ),
                transactionModels: bankDomain.transactionDomains.collect { transactionDomain ->
                    new TransactionModel(
                            id: transactionDomain.id,
                            recipientMobile: transactionDomain.recipientMobile,
                            amount: transactionDomain.amount,
                            dateTime: transactionDomain.dateTime
                    )
                }
        )
    }

    static BankDomain toBankDomain(BankModel bankModel, UserDomain userDomain) {
        new BankDomain(
                bankName: bankModel.bankName,
                accountNumber: bankModel.accountNumber,
                bankPin: bankModel.bankPin,
                transactionLimit: bankModel.transactionLimit,
                balance: bankModel.balance,
                userDomain: userDomain
        )
    }
}
