package com.transaction

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.Date

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val order: Long,

    @Column(nullable = false)
    val description: String,
)

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val count: Long,

    @ManyToOne
    @JoinColumn(name = "category_id")
    val category: Category,
)


@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val fullName: String,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val balance: BigDecimal,
)

@Entity
data class UserPaymentTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val data: Date,
)

@Entity
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = false)
    val totalAmount: BigDecimal,

    @Column(nullable = false)
    val data: Date,
)

@Entity
data class TransactionItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(nullable = false)
    val count: Long,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val totalAmount: BigDecimal,

    @ManyToOne()
    @JoinColumn(name = "transaction_id")
    val transaction: Transaction,
)


