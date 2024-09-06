package com.amzur.test.service

import com.amzur.test.domain.BankDomain
import com.amzur.test.domain.TransactionDomain
import com.amzur.test.domain.UserDomain
import com.amzur.test.model.BankModel
import com.amzur.test.model.TransactionModel
import com.amzur.test.model.UserModel
import grails.gorm.transactions.Transactional

import javax.inject.Singleton

@Singleton
class TransactionService {

    @Transactional
    def createATransaction(TransactionModel transactionModel) {

        Long userDomainId = transactionModel.userModel?.id
        Long bankDomainId = transactionModel.bankModel?.id

        UserDomain userDomain = UserDomain.findById(userDomainId)
        BankDomain bankDomain = BankDomain.findById(bankDomainId)

        if (!userDomain) {
            throw new NoSuchElementException("UserDomain with ID $userDomainId not found")
        }
        if (!bankDomain) {
            throw new NoSuchElementException("BankDomain with ID $bankDomainId not found")
        }

        TransactionDomain transactionDomain = toTransactionDomain(transactionModel, userDomain, bankDomain)

        if (!transactionDomain.save(flush: true)) {
            def errors = transactionDomain.errors.allErrors.collect { error ->
                "${error.field}: ${error.defaultMessage}"
            }
            log.error("Validation errors occurred: ${errors.join(', ')}")
            throw new RuntimeException("Failed to save TransactionDomain: ${errors.join(', ')}")
        }
        return toTransactionModel(transactionDomain)
    }

    @Transactional
    List<TransactionModel> getAllTransactions() {
        List<TransactionDomain> transactionDomains = TransactionDomain.list()
        transactionDomains.collect { toTransactionModel(it) }
    }

    @Transactional
    TransactionModel getATransaction(Long id) {
        TransactionDomain transactionDomain = TransactionDomain.findById(id)
        if (transactionDomain) {
            return toTransactionModel(transactionDomain)
        } else {
            throw new NoSuchElementException("Transaction with ID $id not found")
        }
    }

    static TransactionModel toTransactionModel(TransactionDomain transactionDomain) {
        new TransactionModel(
                id: transactionDomain.id,
                recipientMobile: transactionDomain.recipientMobile,
                amount: transactionDomain.amount,
                dateTime: transactionDomain.dateTime,
                userModel: new UserModel(
                        id: transactionDomain.userDomain.id,
                        mobileNumber: transactionDomain.userDomain.mobileNumber,
                        userPin: transactionDomain.userDomain.userPin,
                        firstName: transactionDomain.userDomain.firstName,
                        lastName: transactionDomain.userDomain.lastName,
                        email: transactionDomain.userDomain.email
                ),
                bankModel: new BankModel(
                        id: transactionDomain.bankDomain.id,
                        bankName: transactionDomain.bankDomain.bankName,
                        accountNumber: transactionDomain.bankDomain.accountNumber,
                        bankPin: transactionDomain.bankDomain.bankPin,
                        transactionLimit: transactionDomain.bankDomain.transactionLimit,
                        balance: transactionDomain.bankDomain.balance
                )
        )
    }

    static TransactionDomain toTransactionDomain(TransactionModel transactionModel, UserDomain userDomain, BankDomain bankDomain) {
        new TransactionDomain(
                recipientMobile: transactionModel.recipientMobile,
                amount: transactionModel.amount,
                dateTime: transactionModel.dateTime,
                userDomain: userDomain,
                bankDomain: bankDomain
        )
    }
}
