package com.transaction

import java.math.BigDecimal


data class AuthDto(
    val username: String,
    val password: String,
)

data class AuthDtoResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshTokenRequestDto(
    val token: String
)

data class RefreshTokenResponseDto(
    val token: String
)

data class CreateUserDto(
    val fullName: String,
    val username: String,
    val password: String,
    val role: Role
) {
    fun toEntity(password: String): User {
        return User(
            fullName,
            username,
            balance = BigDecimal.ZERO,
            password,
            role
        )
    }
}

data class GetUserDto(
    val id: Long,
    val fullName: String,
    val username: String,
    val balance: BigDecimal,
) {
    companion object {
        fun toResponse(user: User): GetUserDto =
            user.run { GetUserDto(id!!, fullName, username, balance) }
    }
}


data class UserUpdateDto(
    val fullName: String?,
    val password: String?,
    var username: String?,
    val role: Role?
)


data class CreateCategoryDto(
    val name: String,
    val orderValue: Long,
    val description: String
) {
    fun toEntity(): Category {
        return Category(name, orderValue, description)
    }
}

data class UpdateCategoryDto(
    val name: String?,
    val orderValue: Long?,
    val description: String?
)


data class GetCategoryDto(
    val id: Long,
    val name: String,
    val orderValue: Long,
    val description: String
) {
    companion object {
        fun toResponse(category: Category): GetCategoryDto = category.run {
            GetCategoryDto(id!!, name, orderValue, description)
        }
    }
}

data class CreateProductDto(
    val name: String,
    val count: Long,
    val categoryId: Long,
) {
    fun toEntity(category: Category) = Product(name, count, category)
}

data class UpdateProductDto(
    val name: String?,
    val count: Long?,
    val categoryId: Long?,
)

data class GetProductDto(
    val id: Long,
    val name: String,
    val count: Long,
    val categoryId: Long,
) {
    companion object {
        fun toResponse(product: Product): GetProductDto = product.run {
            GetProductDto(id!!, name, count, category.id!!)
        }
    }
}

data class CreateUserPaymentTransactionDto(
    val userId: Long,
    val amount: BigDecimal,
)

data class GetUserPaymentTransactionDto(
    val userId: Long,
    val amount: BigDecimal,
) {
    companion object {
        fun toResponse(userPaymentTransaction: UserPaymentTransaction): GetUserPaymentTransactionDto =
            userPaymentTransaction.run {
                return GetUserPaymentTransactionDto(
                    user.id!!,
                    amount
                )
            }
    }
}

data class TransactionDto(
    val userId: Long,
    val totalAmount: BigDecimal,
    val items: List<CreateTransactionItemDto>
)

data class GetTransactionDto(
    val id: Long,
    val userId: Long,
    val totalAmount: BigDecimal
) {
    companion object {
        fun toResponse(transaction: Transaction): GetTransactionDto = transaction.run {
            GetTransactionDto(id!!, user.id!!, totalAmount)
        }
    }
}

data class CreateTransactionItemDto(
    val productId: Long,
    val count: Long,
    val amount: BigDecimal
)

data class GetTransactionItemDto(
    val productId: Long,
    val count: Long,
    val amount: BigDecimal,
    val transactionId: Long
) {
    companion object {
        fun toResponse(transactionItem: TransactionItem): GetTransactionItemDto  = transactionItem.run {
             GetTransactionItemDto(product!!.id!!, count, amount, transaction!!.id!!)
        }
    }
}
