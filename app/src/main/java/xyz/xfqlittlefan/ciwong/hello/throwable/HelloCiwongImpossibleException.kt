package xyz.xfqlittlefan.ciwong.hello.throwable

import androidx.annotation.Keep

@Keep
class HelloCiwongImpossibleException : Exception {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}