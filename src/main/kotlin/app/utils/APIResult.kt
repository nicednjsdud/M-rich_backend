package app.utils

sealed interface APIResult<out T, out E> {
    data class Success<T>(val data: T) : APIResult<T, Nothing>
    data class Error<E>(val error: E) : APIResult<Nothing, E>
}

inline fun <T, E, R> APIResult<T, E>.map(map: (T) -> R): APIResult<R, E> {
    return when (this) {
        is APIResult.Success -> {
            val data = this.data
            val newData = map(data)
            APIResult.Success(newData)
        }

        is APIResult.Error -> this
    }
}

inline fun <T, E> APIResult<T, E>.onSuccess(action: (T) -> Unit): APIResult<T, E> {
    return when (this) {
        is APIResult.Success -> {
            val data = this.data
            action(data)
            this
        }

        is APIResult.Error -> this
    }
}

inline fun <T, E> APIResult<T, E>.onError(action: (E) -> Unit): APIResult<T, E> {
    return when (this) {
        is APIResult.Success -> this
        is APIResult.Error -> {
            val error = this.error
            action(error)
            this
        }
    }
}