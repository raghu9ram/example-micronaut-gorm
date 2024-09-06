package example.micronaut.gorm.domain

import grails.gorm.annotation.Entity

@Entity
class Person {

    String firstName
    String lastName
    int age


    static mapping = {
        id generator:'increment'
        lastName column: "lname", sqlType: "varchar2", length: '350'
    }
    static constraints = {
        firstName nullable: false
        lastName nullable: true

    }



}
