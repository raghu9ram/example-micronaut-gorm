package com.amzur.test

import com.amzur.test.domain.BankDomain
import io.micronaut.context.annotation.Factory
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.ApplicationEvent
import javax.inject.Singleton

@Factory
class StartupInitializer {

    @Singleton
    ApplicationEventListener<ApplicationEvent> applicationStartupListener() {
        return new ApplicationEventListener<ApplicationEvent>() {
            @Override
            void onApplicationEvent(ApplicationEvent event) {
                populatePredefinedBanks()
            }

            private void populatePredefinedBanks() {
                def predefinedBanks = [
                        [bankName: "SBI"],
                        [bankName: "HDFC"],
                        [bankName: "ICICI"]
                ]

                predefinedBanks.each { bank ->
                    if (!BankDomain.findByBankName(bank.bankName)) {
                        new BankDomain(bankName: bank.bankName).save(flush: true)
                    }
                }
            }
        }
    }
}
