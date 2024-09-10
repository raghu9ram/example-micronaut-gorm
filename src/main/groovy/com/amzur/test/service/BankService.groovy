package com.amzur.test.service

import com.amzur.test.domain.BankDomain
import com.amzur.test.domain.UserDomain
import com.amzur.test.model.BankModel
import com.amzur.test.model.TransactionModel
import com.amzur.test.model.UserModel
import grails.gorm.transactions.Transactional
import org.grails.datastore.mapping.core.Session
import org.hibernate.SessionFactory

import javax.inject.Singleton

@Singleton
class BankService {

    SessionFactory sessionFactory

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

        BankDomain bankDomain = toBankDomain(bankModel, userDomain)

        // Check for validation errors
        if (!bankDomain.validate()) {
            def errors = bankDomain.errors.allErrors.collect { error ->
                "${error.field}: ${error.defaultMessage}"
            }
            throw new RuntimeException("Validation errors: ${errors.join(', ')}")
        }

        // Save the bank domain instance
        if (!bankDomain.save(flush: true)) {
            def errors = bankDomain.errors.allErrors.collect { error ->
                "${error.field}: ${error.defaultMessage}"
            }
            throw new RuntimeException("Failed to save BankDomain: ${errors.join(', ')}")
        }

        return toBankModel(bankDomain)
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

    @Transactional
    void updatePrimaryBank(Long userId, boolean primaryBank) {
        if (primaryBank) {
            Session session = sessionFactory.currentSession
            session.createNativeQuery("UPDATE bank_domain SET primary_bank = false WHERE user_domain_id = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate()
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
                primaryBank: bankDomain.primaryBank,
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
                primaryBank: bankModel.primaryBank,
                userDomain: userDomain
        )
    }
}
