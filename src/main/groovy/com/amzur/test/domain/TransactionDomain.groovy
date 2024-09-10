package com.amzur.test.domain


import grails.gorm.annotation.Entity

import java.time.LocalDateTime

@Entity
class TransactionDomain {
    Long recipientMobile
    Integer amount
    LocalDateTime dateTime

    static belongsTo = [ userDomain:UserDomain , bankDomain:BankDomain ]

    static constraints = {
        recipientMobile(nullable: false)
        amount(min: 1)
        dateTime(nullable: false)
    }
}
