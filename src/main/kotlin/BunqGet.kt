import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.http.Pagination
import com.bunq.sdk.model.generated.endpoint.MonetaryAccountBank
import com.bunq.sdk.model.generated.endpoint.Payment
import java.io.File

fun main() {
    loadAPIContext()

    val activeAccounts = getActiveAccounts()

    val paymentsPerAccount = activeAccounts
        .map { it to getPayments(it.id) }
        .toMap()
    
    paymentsPerAccount.forEach { account, transactions -> 
        println(account.description)
        transactions.ifEmpty { println("no payments") }
        transactions.forEach { println("${it.description} ${it.amount.value} ${it.amount.currency}") }
    }
    
    activeAccounts
        .map { "Account ${it.description} has a balance of ${it.balance.value} ${it.balance.currency}" }
        .forEach { println(it) }
}

fun getPayments(id: Int) = Payment
    .list(id, Pagination().urlParamsCountOnly)
    .value

private fun getActiveAccounts(): Set<MonetaryAccountBank> = MonetaryAccountBank
    .list(Pagination().urlParamsCountOnly).value
    .filter { it.status == "ACTIVE" }
    .toSet()


private fun loadAPIContext() {
    val key = File("key.txt").readText()

    val apiContext = ApiContext.create(
        ApiEnvironmentType.PRODUCTION,
        key,
        "Developer device"
    )

    apiContext.save()

    BunqContext.loadApiContext(apiContext)
}
