package com.amzur.test.domain

import grails.gorm.annotation.Entity

@Entity
class UserDomain {
    Long mobileNumber
    Integer userPin
    String firstName
    String lastName
    String email

    static hasMany = [ bankDomains: BankDomain, transactionDomains: TransactionDomain ]
    static mappedBy = [bankDomains: 'userDomain', transactionDomains: 'userDomain']

    static constraints = {
        mobileNumber(nullable: false, unique: true)
        userPin(nullable: false, min:100000, max:999999)
        firstName(nullable: false)
        lastName(nullable: false)
        email(email: true, nullable: false, unique: true)
    }
}
