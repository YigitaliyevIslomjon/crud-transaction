package com.transaction

import org.springframework.stereotype.Service

interface CrudService<T, D> {
    fun add(dto: D): T
    fun edit(id: Long, dto: D): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): T
    fun getAll(): List<T>
}

interface CategoryService : CrudService<Category, CategoryDto>
interface ProductService : CrudService<Product, ProductDto>
interface UserService : CrudService<User, UserDto>
interface UserPaymentTransactionService : CrudService<UserPaymentTransaction, UserPaymentTransactionDto>
interface TransactionService : CrudService<Transaction,TransactionDto>
interface TransactionItemService : CrudService<TransactionItem,TransactionItemDto>


@Service
class CategoryServiceImpl(
    val categoryRepository: CategoryRepository,
) : CategoryService {
    override fun add(dto: CategoryDto): Category {
        TODO("Not yet implemented")
    }

    override fun edit(id: Long, dto: CategoryDto): Result {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long): Result {
        TODO("Not yet implemented")
    }

    override fun getOne(id: Long): Category {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Category> {
        TODO("Not yet implemented")
    }
}

@Service
class ProductServiceImpl(
    val categoryRepository: CategoryRepository,
    val productRepository: ProductRepository,
) : ProductService {
    override fun add(dto: ProductDto): Product {
        TODO("Not yet implemented")
    }

    override fun edit(id: Long, dto: ProductDto): Result {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long): Result {
        TODO("Not yet implemented")
    }

    override fun getOne(id: Long): Product {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Product> {
        TODO("Not yet implemented")
    }

}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {
    override fun add(dto: UserDto): User {
        TODO("Not yet implemented")
    }

    override fun edit(id: Long, dto: UserDto): Result {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long): Result {
        TODO("Not yet implemented")
    }

    override fun getOne(id: Long): User {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<User> {
        TODO("Not yet implemented")
    }
}

@Service
class TransactionServiceImpl(
    val userRepository: UserRepository,
    val transactionRepository: TransactionRepository,
) : TransactionService {
    override fun add(dto: TransactionDto): Transaction {
        TODO("Not yet implemented")
    }

    override fun edit(id: Long, dto: TransactionDto): Result {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long): Result {
        TODO("Not yet implemented")
    }

    override fun getOne(id: Long): Transaction {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Transaction> {
        TODO("Not yet implemented")
    }
}

@Service
class TransactionItemServiceImpl(
    val transactionItemRepository: TransactionItemRepository,
    val transactionRepository: TransactionRepository,
    val productRepository: ProductRepository,
) : TransactionItemService {
    override fun add(dto: TransactionItemDto): TransactionItem {
        TODO("Not yet implemented")
    }

    override fun edit(id: Long, dto: TransactionItemDto): Result {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long): Result {
        TODO("Not yet implemented")
    }

    override fun getOne(id: Long): TransactionItem {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<TransactionItem> {
        TODO("Not yet implemented")
    }
}

@Service
class UserPaymentTransactionServiceImpl(
    val userRepository: UserRepository,
    val userPaymentTransactionService: UserPaymentTransactionService

): UserPaymentTransactionService {
    override fun add(dto: UserPaymentTransactionDto): UserPaymentTransaction {
        TODO("Not yet implemented")
    }

    override fun edit(id: Long, dto: UserPaymentTransactionDto): Result {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long): Result {
        TODO("Not yet implemented")
    }

    override fun getOne(id: Long): UserPaymentTransaction {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<UserPaymentTransaction> {
        TODO("Not yet implemented")
    }

}

