package com.transaction

import org.springframework.data.jpa.repository.JpaRepository


interface CategoryRepository : JpaRepository<Category,Long> {
}

interface ProductRepository : JpaRepository<Product,Long> {
}

interface UserRepository : JpaRepository<User,Long> {
}

interface UserPaymentTransactionRepository : JpaRepository<UserPaymentTransaction,Long> {
}

interface TransactionRepository : JpaRepository< Transaction,Long> {
}

interface TransactionItemRepository : JpaRepository<TransactionItem,Long> {
}