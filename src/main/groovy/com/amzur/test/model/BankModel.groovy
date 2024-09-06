package com.amzur.test.model

class BankModel {
    Long id
    String bankName
    Long accountNumber
    Integer bankPin
    Integer transactionLimit
    Integer balance
    UserModel userModel
    List<TransactionModel> transactionModels = []
}
