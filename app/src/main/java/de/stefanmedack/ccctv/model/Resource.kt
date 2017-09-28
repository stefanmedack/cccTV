package de.stefanmedack.ccctv.model

@Suppress("unused")
sealed class Resource<out T> {
    data class Error<out T>(val msg: String, val data: T? = null) : Resource<T>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
}
