package com.transaction

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class ExceptionControllerAdvice(
    private val errorMessageSource: ResourceBundleMessageSource
) {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<*> {
        return ResponseEntity.badRequest().body(e.toResponse(errorMessageSource))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFoundException(ex: NoHandlerFoundException): ResponseEntity<String> {
        return ResponseEntity("This URL does not exist", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: ExpiredJwtException): ResponseEntity<String> {
        return ResponseEntity("expired token ${ex.message}", HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    protected fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException?, headers: HttpHeaders?, status: HttpStatus?, request: WebRequest?
    ): ResponseEntity<Any> {
        val errorMessage = "Unsupported media type. Please use a valid media type."
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(BaseMessage(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), errorMessage))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): Map<String, String> {
        val errors = ex.bindingResult.allErrors.map { error -> error.defaultMessage }
        return mapOf("errors" to errors.joinToString(", "))
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<BaseMessage> {
        val errorMessage = BaseMessage(
            HttpStatus.FORBIDDEN.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(errorMessage)
    }
}

sealed class BaseException : RuntimeException() {
    abstract fun errorCode(): ErrorCode
    open fun getErrorMessageArguments(): Array<Any?>? = null
    fun toResponse(messageSource: MessageSource): BaseMessage {
        return BaseMessage(
            errorCode().code,
            messageSource
                .getMessage(errorCode().name, getErrorMessageArguments(), LocaleContextHolder.getLocale())
        )
    }
}

class UsernameExistException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.USERNAME_EXIST
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class UserNotFoundException(private val msg: String) : BaseException() {
    override fun errorCode(): ErrorCode = ErrorCode.USER_NOT_FOUND
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class CategoryNotFoundException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.CATEGORY_NOT_FOUND
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class ProductNotFoundException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.PRODUCT_NOT_FOUND
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class UserPaymentTransactionNotFoundException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.USER_PAYMENT_TRANSACTION_NOT_FOUND
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class TransactionNotFoundException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.TRANSACTION_NOT_FOUND
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class TransactionItemNotFoundException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.TRANSACTION_ITEM_NOT_FOUND
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class NotEnoughMoneyException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.NOT_ENOUGH_MONEY
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

class UserAuthorizationFailureException(private val msg: String) : BaseException() {
    override fun errorCode() = ErrorCode.USER_AUTHORIZATION_FAILURE_EXCEPTION
    override fun getErrorMessageArguments(): Array<Any?> = arrayOf(msg)
}

