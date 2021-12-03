package net.chrisrichardson.liveprojects.servicetemplate.domain

import net.chrisrichardson.liveprojects.servicetemplate.domain.AccountCommandResult.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Account (var balance : Long,
               var owner : String,
               @Id @GeneratedValue var id : Long? = null) {
    constructor() : this(0, "")

    fun debit(amount: Long) : AccountCommandResult {
        if (amount <= 0)
            return AmountNotGreaterThanZero(amount)

        if (amount > balance)
            return BalanceExceeded(amount, balance)

        balance -= amount

        return Success
    }

    fun credit(amount: Long) : AccountCommandResult {
        if (amount <= 0)
            return AmountNotGreaterThanZero(amount)

        balance += amount

        return Success
    }

    fun cancel(currentUserId: String): AccountCommandResult {
        if (owner != currentUserId)
            return Unauthorized
        // Change status
        return Success
    }

}

sealed class AccountCommandResult {
    object Success : AccountCommandResult()
    data class AmountNotGreaterThanZero(val amount : Long) : AccountCommandResult()
    data class BalanceExceeded(val amount : Long, val balance : Long) : AccountCommandResult()
    object Unauthorized : AccountCommandResult()

}

