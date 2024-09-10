package com.amzur.test.service

import com.amzur.test.domain.BankDomain
import com.amzur.test.domain.TransactionDomain
import com.amzur.test.domain.UserDomain
import com.amzur.test.model.BankModel
import com.amzur.test.model.TransactionModel
import com.amzur.test.model.UserModel
import grails.gorm.transactions.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Singleton
import java.time.LocalDateTime

@Singleton
class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class)

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
    def performTransfer(Long fromUserId, Long fromBankId, Long toUserId, BigDecimal amount, String bankPin) {
        UserDomain fromUser = UserDomain.findById(fromUserId)
        UserDomain toUser = UserDomain.findById(toUserId)

        if (!fromUser || !toUser) {
            throw new NoSuchElementException("User not found")
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero")
        }

        // Find the selected bank account for the sender
        BankDomain fromBank = fromUser.bankDomains.find { it.id == fromBankId }
        if (!fromBank) {
            throw new NoSuchElementException("Bank account with ID $fromBankId not found for the sender")
        }

        // Validate bank pin for the sender's bank account
        if (!validateBankPin(fromBank, bankPin)) {
            throw new SecurityException("Invalid bank pin")
        }

        // Find the primary bank account for the receiver
        BankDomain toBank = toUser.bankDomains.find { it.primaryBank }
        if (!toBank) {
            throw new NoSuchElementException("Primary bank account for receiver not found")
        }

        if (fromBank.balance < amount) {
            throw new IllegalStateException("Insufficient balance")
        }

        // Perform the transfer
        fromBank.balance -= amount
        toBank.balance += amount

        if (!fromBank.save(flush: true) || !toBank.save(flush: true)) {
            throw new RuntimeException("Failed to update bank accounts")
        }

        // Convert the current date to LocalDateTime
        LocalDateTime currentDateTime = LocalDateTime.now()

        TransactionDomain transaction = new TransactionDomain(
                recipientMobile: toUser.mobileNumber,
                amount: amount,
                dateTime: currentDateTime, // Use LocalDateTime
                userDomain: fromUser,
                bankDomain: fromBank
        )

        if (!transaction.save(flush: true)) {
            def errors = transaction.errors.allErrors.collect { error ->
                "${error.field}: ${error.defaultMessage}"
            }
            log.error("Validation errors occurred: ${errors.join(', ')}")
            throw new RuntimeException("Failed to save TransactionDomain: ${errors.join(', ')}")
        }

        return toTransactionModel(transaction)
    }

    private boolean validateBankPin(BankDomain bank, String bankPin) {
        if (bank == null) {
            log.error("Bank account not found during bank pin validation.")
            return false
        }
        if (bankPin == null) {
            log.error("Bank pin is null.")
            return false
        }

        String storedPin = bank.bankPin.toString()
        boolean isValid = storedPin.equals(bankPin)

        log.info("Validating bank pin: storedPin=${storedPin}, inputPin=${bankPin}, isValid=${isValid}")
        return isValid
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
                        balance: transactionDomain.bankDomain.balance,
                        primaryBank: transactionDomain.bankDomain.primaryBank
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

    def findTransactionsAfter(LocalDateTime dateTime) {
        TransactionDomain.findAllByDateTimeGreaterThan(dateTime)
    }

}
