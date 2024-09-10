package com.amzur.test.model

import java.time.LocalDateTime

class TransactionModel {
    Long id
    Long recipientMobile
    Integer amount
    LocalDateTime dateTime
    UserModel userModel
    BankModel bankModel
}
