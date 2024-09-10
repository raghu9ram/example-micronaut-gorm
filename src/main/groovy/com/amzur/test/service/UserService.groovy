package com.amzur.test.service

import com.amzur.test.domain.BankDomain
import com.amzur.test.domain.TransactionDomain
import com.amzur.test.domain.UserDomain
import com.amzur.test.handlers.UserNotFound
import com.amzur.test.model.BankModel
import com.amzur.test.model.TransactionModel
import com.amzur.test.model.UserModel
import grails.gorm.transactions.Transactional

import javax.inject.Singleton

@Singleton
class UserService {

    @Transactional
    def createAUser(UserModel userModel) {
        UserDomain userDomain = toUserDomain(userModel)
        userDomain.save(flush: true)
        return toUserModel(userDomain)
    }

    @Transactional
    UserModel getAUser(Long id) {
        UserDomain userDomain = UserDomain.findById(id)
        if(userDomain) {
            return toUserModel(userDomain)
        } else {
            throw new NoSuchElementException("User with ID $id not found")
        }
    }

    @Transactional
    def updateAUser(Long id, UserModel userModel) {
        UserDomain userDomain = UserDomain.findById(id)

        if (userDomain) {
            userDomain.mobileNumber = userModel.mobileNumber
            userDomain.userPin = userModel.userPin
            userDomain.firstName = userModel.firstName
            userDomain.lastName = userModel.lastName
            userDomain.email = userModel.email

            userDomain.save(flush: true)

            return toUserModel(userDomain)
        } else {
            throw new NoSuchElementException("User with ID $id not found")
        }
    }


    static UserModel toUserModel(UserDomain userDomain) {
        new UserModel(
                id: userDomain.id,
                mobileNumber: userDomain.mobileNumber,
                userPin: userDomain.userPin,
                firstName: userDomain.firstName,
                lastName: userDomain.lastName,
                email: userDomain.email,
                bankModels: userDomain.bankDomains.collect { bankDomain ->
                    new BankModel(
                            id: bankDomain.id,
                            bankName: bankDomain.bankName,
                            accountNumber: bankDomain.accountNumber,
                            bankPin: bankDomain.bankPin,
                            transactionLimit: bankDomain.transactionLimit,
                            balance: bankDomain.balance,
                            primaryBank: bankDomain.primaryBank
                    )
                },
                transactionModels: userDomain.transactionDomains.collect { transactionDomain ->
                    new TransactionModel(
                            id: transactionDomain.id,
                            recipientMobile: transactionDomain.recipientMobile,
                            amount: transactionDomain.amount,
                            dateTime: transactionDomain.dateTime
                    )
                }
        )
    }

    static UserDomain toUserDomain(UserModel userModel) {
        new UserDomain(
                mobileNumber: userModel.mobileNumber,
                userPin: userModel.userPin,
                firstName: userModel.firstName,
                lastName: userModel.lastName,
                email: userModel.email
        )
    }

    @Transactional
    def login(Long mobileNumber, Integer userPin) {
        UserDomain userDomain = UserDomain.findByMobileNumberAndUserPin(mobileNumber, userPin)
        if(userDomain) {
            return toUserModel(userDomain)
        } else {
            throw new UserNotFound("Invalid Credentials")
        }
    }

    @Transactional
    List<BankModel> getUserBanks(Long userId) {
        UserDomain userDomain = UserDomain.findById(userId)
        if (userDomain) {
            return userDomain.bankDomains.collect { BankDomain bankDomain ->
                new BankModel(
                        id: bankDomain.id,
                        bankName: bankDomain.bankName,
                        accountNumber: bankDomain.accountNumber,
                        bankPin: bankDomain.bankPin,
                        transactionLimit: bankDomain.transactionLimit,
                        balance: bankDomain.balance,
                        primaryBank: bankDomain.primaryBank,
                        userModel: toUserModel(userDomain)
                )
            }
        } else {
            throw new NoSuchElementException("User with ID $userId not found")
        }
    }

    @Transactional
    List<TransactionModel> getUserTransactions(Long userId) {
        UserDomain userDomain = UserDomain.findById(userId)
        if(userDomain) {
            return userDomain.transactionDomains.collect { TransactionDomain transactionDomain ->
                new TransactionModel(
                        id: transactionDomain.id,
                        recipientMobile: transactionDomain.recipientMobile,
                        amount: transactionDomain.amount,
                        dateTime: transactionDomain.dateTime,
                        userModel: toUserModel(userDomain)
                )
            }
        } else {
            throw new NoSuchElementException("User with ID $userId not found")
        }
    }

    @Transactional
    UserModel getUserByMobileNumber(String mobileNumber) {
        UserDomain userDomain = UserDomain.findByMobileNumber(mobileNumber)
        if (userDomain) {
            return toUserModel(userDomain)
        } else {
            throw new NoSuchElementException("User with mobile number $mobileNumber not found")
        }
    }
}
