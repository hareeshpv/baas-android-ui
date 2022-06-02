package com.payu.baas.core.singleton

open class SingletonHolder<out T: Any, in A, in B>(creator: (A,B?) -> T) {
    private var creator: ((A,B?) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(context: A, isProduction: B? = null): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(context,isProduction)
                instance = created
                creator = null
                created
            }
        }
    }
}