package com.transaction

import java.math.BigDecimal

enum class UserRole{
    USER,
    ADMIN,
    SUPER_ADMIN
}
data class AuthDto(
    val username: String,
    val password: String,
)
data class AuthDtoResponse (
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshTokenRequestDto(
    val token: String
)

data class RefreshTokenResponseDto(
    val token: String
)

data class UserDto(
    val fullName: String,
    val username: String,
    val password: String,
    val roles: List<Long>
)
data class UserEditDto(
    val fullName: String,
    val password: String,
    val roles: List<Long>
)

data class UserDtoResponse(
    val id: Long,
    val fullName: String,
    val username: String,
    val balance: BigDecimal,
    val roles: Set<Role>
) {
    companion object {
        fun toResponse(user: User): UserDtoResponse {
            return UserDtoResponse(
                user.id!!,
                user.fullName,
                user.username,
                user.balance,
                user.roles
            )
        }
    }
}


data class RoleDto(
    val name: UserRole,
)

data class RoleDtoResponse(
    val id: Long,
    val name: UserRole,
) {
    companion object {
        fun toResponse(role: Role): RoleDtoResponse {
            return RoleDtoResponse(role.id!!, role.name)
        }
    }
}

data class CategoryDto(
    val name: String,
    val orderValue: Long,
    val description: String
)

data class CategoryDtoResponse(
    val name: String,
    val orderValue: Long,
    val description: String
) {
    companion object {
        fun toResponse(category: Category): CategoryDtoResponse {
            return CategoryDtoResponse(
                category.name,
                category.orderValue,
                category.description
            )
        }
    }
}

data class ProductDto(
    val name: String,
    val count: Long,
    val categoryId: Long,
)

data class ProductDtoResponse(
    val name: String,
    val count: Long,
    val categoryId: Long,
) {
    companion object {
        fun toResponse(product: Product): ProductDtoResponse {
            return ProductDtoResponse(
                product.name,
                product.count,
                product.category.id!!
            )
        }
    }
}
data class UserPaymentTransactionDto(
    val userId: Long,
    val amount: BigDecimal,
)

data class UserPaymentTransactionDtoResponse(
    val userId: Long,
    val amount: BigDecimal,
) {
    companion object {
        fun toResponse(userPaymentTransaction: UserPaymentTransaction): UserPaymentTransactionDtoResponse {
            return UserPaymentTransactionDtoResponse(
                userPaymentTransaction.user.id!!,
                userPaymentTransaction.amount
            )
        }
    }
}

data class TransactionDto(
    val userId: Long,
    val totalAmount: BigDecimal,
    val items: List<TransactionItemDto>
)

data class TransactionDtoResponse(
    val id: Long,
    val userId: Long,
    val totalAmount: BigDecimal
) {
    companion object {
        fun toResponse(transaction: Transaction): TransactionDtoResponse {
            return TransactionDtoResponse(
                transaction.id!!,
                transaction.user.id!!,
                transaction.totalAmount
            )
        }
    }
}

data class TransactionItemDto(
    val productId: Long,
    val count: Long,
    val amount: BigDecimal
)

data class TransactionItemDtoResponse(
    val productId: Long,
    val count: Long,
    val amount: BigDecimal,
    val transactionId: Long
) {
    companion object {
        fun toResponse(transactionItem: TransactionItem): TransactionItemDtoResponse {
            return TransactionItemDtoResponse(
                transactionItem.product!!.id!!,
                transactionItem.count,
                transactionItem.amount,
                transactionItem.transaction!!.id!!
            )
        }
    }
}

data class Result(
    val message: String,
)