import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.http.Pagination
import com.bunq.sdk.model.generated.endpoint.MonetaryAccountBank
import java.io.File

fun main(args: Array<String>) {
    loadAPIContext()

    val pagination = Pagination()
    pagination.count = 1
    
    MonetaryAccountBank
        .list(pagination.urlParamsCountOnly).value
        .filter { it.status == "ACTIVE" }
        .map { "Account ${it.id} has a balance of ${it.balance.value} ${it.balance.currency}" }
        .forEach { println(it) }
}

private fun loadAPIContext() {
    val key = File("key.txt").readText()

    val apiContext = ApiContext.create(
        ApiEnvironmentType.SANDBOX,
        key,
        "Developer device"
    )

    apiContext.save()

    BunqContext.loadApiContext(apiContext)
}
