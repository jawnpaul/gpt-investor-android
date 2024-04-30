package com.thejawnpaul.gptinvestor.core.functional

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    data object NetworkConnection : Failure()

    data object DataError : Failure()

    data object ServerError : Failure()

    data object UnAuthorizedError : Failure()

    data object UnAvailableError : Failure()

    data object InternalError : Failure()

    data object FailedPreconditionError : Failure()

    data object NotFoundError : Failure()

    data object InvalidArgumentError : Failure()

    data object UnAuthenticatedError : Failure()

    data object UnknownError : Failure()

    data object AlreadyExists : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}
