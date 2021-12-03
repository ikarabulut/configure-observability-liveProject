package net.chrisrichardson.liveprojects.servicetemplate.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Supplier

interface AccountService {
    fun createAccount(initialBalance: Long) : AccountServiceCommandResult
    fun findAccount(id: Long): Optional<Account>
    fun debit(id: Long, amount: Long): AccountServiceCommandResult
    fun credit(id: Long, amount: Long): AccountServiceCommandResult
    fun findAllAccounts(): List<Account>
    fun cancel(id: Long): AccountServiceCommandResult
}

@Service
@Transactional
class AccountServiceImpl @Autowired constructor(val accountRepository: AccountRepository,
                                                val authenticatedUserSupplier: AuthenticatedUserSupplier = AuthenticatedUserSupplier.EMPTY_SUPPLIER,
                                                val accountServiceObserver: AccountServiceObserver ?) : AccountService {


    override fun createAccount(initialBalance: Long) : AccountServiceCommandResult {
        if (initialBalance > 0) {
            accountServiceObserver?.noteOrderCreated()
            val owner = currentUserId()
            val account = Account(initialBalance, owner)
            return AccountServiceCommandResult.Success(accountRepository.save(account))
        } else {
            return AccountServiceCommandResult.AmountNotGreaterThanZero(initialBalance)
        }
    }

    private fun currentUserId(): String = authenticatedUserSupplier.get().id

    override fun findAccount(id: Long): Optional<Account> {
        return accountRepository.findById(id)
    }

    override fun debit(id: Long, amount: Long): AccountServiceCommandResult {
        return accountRepository.findById(id).map { account ->
            when (val outcome = account.debit(amount)) {
                is AccountCommandResult.Success -> {
                    accountServiceObserver?.noteSuccessfulDebit()
                    AccountServiceCommandResult.Success(account)
                }
                is AccountCommandResult.AmountNotGreaterThanZero -> {
                    accountServiceObserver?.noteFailedDebit()
                    AccountServiceCommandResult.AmountNotGreaterThanZero(amount)
                }
                is AccountCommandResult.BalanceExceeded -> {
                    accountServiceObserver?.noteFailedDebit()
                    AccountServiceCommandResult.BalanceExceeded(amount, account.balance)
                }
                else ->
                    AccountServiceCommandResult.Unexpected(outcome)
            }
        }.orElseGet { ->
            accountServiceObserver?.noteFailedDebit()
            AccountServiceCommandResult.AccountNotFound
        }
    }


    override fun credit(id: Long, amount: Long): AccountServiceCommandResult {
        return accountRepository.findById(id).map { account ->
            when (val outcome = account.credit(amount)) {
                is AccountCommandResult.Success -> {
                    accountServiceObserver?.noteSuccessfulCredit()
                    AccountServiceCommandResult.Success(account)
                }
                is AccountCommandResult.AmountNotGreaterThanZero -> {
                    accountServiceObserver?.noteFailedCredit()
                    AccountServiceCommandResult.AmountNotGreaterThanZero(amount)
                }
                else ->
                    AccountServiceCommandResult.Unexpected(outcome)
            }
        }.orElseGet { ->
            accountServiceObserver?.noteFailedCredit()
            AccountServiceCommandResult.AccountNotFound
        }
    }

    override fun findAllAccounts(): List<Account> {
        val result: MutableList<Account> = mutableListOf()
        accountRepository.findAll().toCollection(result)
        return result
    }

    override fun cancel(id: Long): AccountServiceCommandResult {
        return accountRepository.findById(id).map { account ->
            when (val outcome = account.cancel(currentUserId())) {
                is AccountCommandResult.Success -> {
                    accountServiceObserver?.noteSuccessfulCancel()
                    AccountServiceCommandResult.Success(account)
                }
                AccountCommandResult.Unauthorized -> {
                    accountServiceObserver?.noteUnauthorized()
                    AccountServiceCommandResult.Unauthorized
                }
                else ->
                    AccountServiceCommandResult.Unexpected(outcome)
            }
        }.orElseGet { ->
            accountServiceObserver?.noteCancelFailed()
            AccountServiceCommandResult.AccountNotFound
        }
    }
}

interface AccountServiceObserver {

    fun noteOrderCreated()
    fun noteSuccessfulDebit()
    fun noteFailedDebit()
    fun noteFailedCredit()
    fun noteSuccessfulCredit()
    fun noteUnauthorized()
    fun noteCancelFailed()
    fun noteSuccessfulCancel()
}

sealed class AccountServiceCommandResult {
    data class Success(val account: Account) : AccountServiceCommandResult()
    data class AmountNotGreaterThanZero(val amount: Long) : AccountServiceCommandResult()
    data class BalanceExceeded(val amount: Long, val balance: Long) : AccountServiceCommandResult()
    data class Unexpected(val outcome: AccountCommandResult) : AccountServiceCommandResult()

    object AccountNotFound : AccountServiceCommandResult()
    object Unauthorized : AccountServiceCommandResult()

}


interface AuthenticatedUserSupplier : Supplier<AuthenticatedUser> {
    object EMPTY_SUPPLIER : AuthenticatedUserSupplier {
        override fun get() = AuthenticatedUser("nullId", emptySet())

    }
}

data class AuthenticatedUser(val id: String, val roles: Set<String>)

