package com.transaction

import io.jsonwebtoken.ExpiredJwtException
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
class ExceptionControllerAdvice {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(baseException: BaseException): ResponseEntity<*> {
        return ResponseEntity.badRequest().body(baseException.getModel())
    }

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEntityNotFoundExceptionError(ex: Exception): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(errorMessage)
    }

//    @ExceptionHandler
//    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorMessageModel> {
//        val errorMessage = ErrorMessageModel(
//            HttpStatus.NOT_FOUND.value(),
//            ex.message
//        )
//        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
//    }

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
            .body(ErrorMessageModel(message = errorMessage, status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): Map<String, String> {
        println(ex.message)
        val errors = ex.bindingResult.allErrors.map { error -> error.defaultMessage }
        return mapOf("errors" to errors.joinToString(", "))
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.FORBIDDEN.value(),
            ex.message
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(errorMessage)
    }

//    @ExceptionHandler(Exception::class)
//    fun handleOtherException(ex: Exception): ResponseEntity<ErrorMessageModel> {
//        val exceptionId = UUID.randomUUID()
//        println("${exceptionId} : [${ex.stackTraceToString()}]")
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//            .body(ErrorMessageModel(message = "Qanaqadur xatoli($exceptionId)", status = HttpStatus.CONFLICT.value()))
//    }

}

abstract class BaseException(private val msg: String? = null) : RuntimeException(msg) {
    abstract fun errorCode(): ErrorCode
    abstract fun getModel(): ErrorMessageModel
}

class UsernameExistException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.USERNAME_EXIST
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}
class UserNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.USER_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }

}

class CategoryNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.CATEGORY_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}
class ProductNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.PRODUCT_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class UserPaymentTransactionNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.USER_PAYMENT_TRANSACTION_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class TransactionNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.TRANSACTION_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class TransactionItemNotFoundException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.TRANSACTION_ITEM_NOT_FOUND
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}
class NotEnoughMoneyException(msg: String) : BaseException(msg) {
    override fun errorCode() = ErrorCode.NOT_ENOUGH_MONEY
    override fun getModel(): ErrorMessageModel {
        return ErrorMessageModel(errorCode().code, message)
    }
}

class EntityNotFoundException(message: String) : RuntimeException(message)
class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null
)