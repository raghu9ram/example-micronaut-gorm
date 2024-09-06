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
        if(userDomain) {
            userDomain.mobileNumber = userModel.mobileNumber
            userDomain.userPin = userModel.userPin
            userDomain.firstName = userModel.firstName
            userDomain.lastName = userModel.lastName
            userDomain.email = userModel.email
            if (userModel.banks) {
                userModel.banks.each { bankModel ->
                    def bankDomain = new BankDomain(
                            bankName: bankModel.bankName,
                            accountNumber: bankModel.accountNumber,
                            bankPin: bankModel.bankPin,
                            transactionLimit: bankModel.transactionLimit,
                            balance: bankModel.balance,
                            userDomain: userDomain
                    )
                    userDomain.addToBanks(bankDomain)
                }
            }

            if (userModel.transactions) {
                userModel.transactions.each { transactionModel ->
                    def transactionDomain = new TransactionDomain(
                            recipientMobile: transactionModel.recipientMobile,
                            amount: transactionModel.amount,
                            dateTime: transactionModel.dateTime,
                            userDomain: userDomain
                    )
                    userDomain.addToTransactions(transactionDomain)
                }
            }
            userDomain.save(flush: true)
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
                            bankName: bankDomain.bankName,
                            accountNumber: bankDomain.accountNumber,
                            bankPin: bankDomain.bankPin,
                            transactionLimit: bankDomain.transactionLimit,
                            balance: bankDomain.balance
                    )
                },
                transactionModels: userDomain.transactionDomains.collect { transactionDomain ->
                    new TransactionModel(
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
    def login(Long mobileNumber, Number userPin) {
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
                        userModel: new UserModel(
                                id: userDomain.id,
                                mobileNumber: userDomain.mobileNumber,
                                userPin: userDomain.userPin,
                                firstName: userDomain.firstName,
                                lastName: userDomain.lastName,
                                email: userDomain.email
                        )
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
                        userModel: new UserModel(
                                id: userDomain.id,
                                mobileNumber: userDomain.mobileNumber,
                                userPin: userDomain.userPin,
                                firstName: userDomain.firstName,
                                lastName: userDomain.lastName,
                                email: userDomain.email
                        )
                )
            }
        } else {
            throw new NoSuchElementException("User with ID $id not found")
        }
    }
}
