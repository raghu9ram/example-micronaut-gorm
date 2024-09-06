package com.amzur.test.model

class UserModel {
    Long id
    Long mobileNumber
    Integer userPin
    String firstName
    String lastName
    String email

    List<BankModel> bankModels = []
    List<TransactionModel> transactionModels = []
}
