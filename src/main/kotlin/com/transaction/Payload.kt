package com.transaction

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*

data class CategoryDto(
    @field:NotBlank()
    val name: String,

    @field:NotNull()
    val order: Long,

    @field:NotBlank()
    val description: String
)

data class ProductDto(

    @field:NotBlank()
    val name: String,

    @field:NotNull()
    val count: Long,

    @field:NotNull()
    val categoryId: Long,
)

data class UserDto(

    @field:NotBlank
    val fullName: String,

    @field:NotBlank
    val username: String,

    @field:NotNull
    val balance: BigDecimal
)


data class UserPaymentTransactionDto(

    @field:NotNull
    val userId: Long,

    @field:NotNull
    val amount: BigDecimal,

    @field:NotNull
    val data: Date,
)

data class TransactionDto(

    @field:NotNull
    val userId: Long ,

    @field:NotNull
    val totalAmount: BigDecimal,

    @field:NotNull
    val data: Date,
)

data class TransactionItemDto(

    @field:NotNull
    val productId: Long,

    @field:NotNull
    val count: Long,

    @field:NotNull
    val amount: BigDecimal,

    @field:NotNull
    val totalAmount: BigDecimal,

    @field:NotNull
    val transactionId: Long,
)

data class Result(
    val message: String,
    val success: Boolean = false
)