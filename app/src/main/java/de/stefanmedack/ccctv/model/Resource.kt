package de.stefanmedack.ccctv.model

sealed class Resource<out T> {
    open val data: T? = null

    data class Error<out T>(val msg: String, override val data: T? = null) : Resource<T>()
    data class Loading<out T>(override val data: T? = null) : Resource<T>()
    data class Success<out T>(override val data: T) : Resource<T>()
}
