package com.amzur.test.domain

import grails.gorm.annotation.Entity

@Entity
class BankDomain {
    Long id
    String bankName
    Long accountNumber
    Integer bankPin
    Integer transactionLimit
    Integer balance
    Boolean primaryBank = false

    static belongsTo = [userDomain: UserDomain]
    static hasMany = [transactionDomains: TransactionDomain]
    static mappedBy = [transactionDomains: 'bankDomain']

    static constraints = {
        bankName(nullable: false)
        accountNumber(nullable: false, unique: true)
        bankPin(nullable: false, min: 1000, max: 9999)
        transactionLimit(min: 0)
        balance(min: 0)
        primaryBank(nullable: false)
    }
}
