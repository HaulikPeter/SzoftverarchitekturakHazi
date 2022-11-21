package hu.bme.vik.aut.service

interface OnResultListener<T> {
    fun onSuccess(result: T)
    fun onError(exception: Exception)
}