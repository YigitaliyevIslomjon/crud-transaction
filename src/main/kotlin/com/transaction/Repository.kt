package com.transaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CategoryRepository : JpaRepository<Category, Long> {
    /*    @Query(value = "select a from Category a")//JPQL - java persistence query language
        fun jpqlGetAll(): List<Category>

        @Query(value = "select * from category group by ", nativeQuery = true)// Nativa query
        fun getAll(): List<Category>*/
}

interface ProductRepository : JpaRepository<Product, Long> {}
interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User?
}

interface UserPaymentTransactionRepository : JpaRepository<UserPaymentTransaction, Long> {
    @Query(
        value = "SELECT upt.* FROM user_payment_transaction upt INNER JOIN users u ON upt.user_id = u.id WHERE u.username = :username",
        nativeQuery = true
    )
    fun getAllUserPaymentTransactionByUserName(username: String): List<UserPaymentTransaction>
}

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun deleteByUser(user: User)
}

interface TransactionItemRepository : JpaRepository<TransactionItem, Long> {
    @Query(
        value = "SELECT ti.* FROM transaction_item ti " +
                "INNER JOIN transaction t ON ti.transaction_id = t.id " +
                "INNER JOIN users u ON t.user_id = u.id " +
                "WHERE u.username = :username",
        nativeQuery = true
    )
    fun findAllByUsername(username: String): List<TransactionItem>
}

interface RoleRepository : JpaRepository<Role, Long> {}

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
}