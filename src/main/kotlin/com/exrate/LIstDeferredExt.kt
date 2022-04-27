package com.exrate

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.selects.whileSelect
import java.util.concurrent.CopyOnWriteArraySet

/**
 *  Waits for first successfully completed Deferred and return its result
 *  or failure of last executed coroutine
 */
@Suppress("OPT_IN_USAGE")
suspend fun <T> List<Deferred<Result<T>>>.awaitFirstSuccess(): Result<T> {
    val deferredsLeft = CopyOnWriteArraySet(this)

    var result: Result<T>? = null

    whileSelect {
        deferredsLeft.forEach { deferred ->
            deferred.onAwait { it ->
                deferredsLeft.remove(deferred)
                result = it
                if (it.isSuccess) {
                    false
                } else {
                    deferredsLeft.isNotEmpty()
                }
            }
        }
    }
    return result?.let { result }
        ?: Result.failure(RuntimeException("Unable to get root cause, check logs for more details"))
}
