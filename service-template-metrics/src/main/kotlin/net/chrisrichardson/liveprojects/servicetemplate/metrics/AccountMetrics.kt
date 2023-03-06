package net.chrisrichardson.liveprojects.servicetemplate.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import net.chrisrichardson.liveprojects.servicetemplate.domain.AccountServiceObserver
import org.springframework.stereotype.Component

@Component
class AccountMetrics (var registry: MeterRegistry) : AccountServiceObserver {
    val accountCounter: Counter = registry.counter("account.created.total")

    override fun noteAccountCreated() {
        accountCounter.increment()
    }

    override fun noteSuccessfulDebit() {
        TODO("Not yet implemented")
    }

    override fun noteFailedDebit() {
        TODO("Not yet implemented")
    }

    override fun noteFailedCredit() {
        TODO("Not yet implemented")
    }

    override fun noteSuccessfulCredit() {
        TODO("Not yet implemented")
    }

    override fun noteUnauthorizedAccountAccess() {
        TODO("Not yet implemented")
    }

}
