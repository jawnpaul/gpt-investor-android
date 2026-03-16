package com.thejawnpaul.gptinvestor.core.baseusecase

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in Params, out Type>(
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
)
    where Type : Any {

    abstract suspend fun run(params: Params): Flow<Either<Failure, Type>>

    open operator fun invoke(params: Params, onResult: (Either<Failure, Type>) -> Unit = {}) {
        coroutineScope.launch {
            withContext(dispatcher) {
                val job = async { run(params) }.await()
                job.catch {
                    onResult(Either.Left(Failure.ServerError))
                }.collect { res ->
                    onResult(res)
                }
            }
        }
    }
}
