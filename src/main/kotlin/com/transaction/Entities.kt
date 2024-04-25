package com.transaction

import jakarta.persistence.*
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.util.Date


@Entity
class RefreshToken(
    val token: String,
    val userDetails: UserDetails,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
)

@Entity
class Category(
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var orderValue: Long,
    @Column(nullable = false) var description: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL]) val products: List<Product> = ArrayList(),
)

@Entity
class Product(
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var count: Long,
    @ManyToOne var category: Category,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL]) val transactionItems: List<TransactionItem> = ArrayList(),
)

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false) var fullName: String,
    @Column(nullable = false, unique = true) var username: String,
    @Column(nullable = false) var balance: BigDecimal,
    @Column(nullable = false) var password: String,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var role: Role,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL]) val transactions: List<Transaction> = ArrayList(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL]) var userPaymentTransactions: List<UserPaymentTransaction> = ArrayList(),
)


@Entity
data class UserPaymentTransaction(
    @ManyToOne val user: User, @Column(nullable = false) val amount: BigDecimal,
    @Column(nullable = false) val date: Date = Date(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

@Entity
class Transaction(
    @ManyToOne(fetch = FetchType.LAZY) val user: User,
    @Column(nullable = false) val totalAmount: BigDecimal,
    @Column(nullable = false) val date: Date,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @OneToMany(mappedBy = "transaction", cascade = [CascadeType.ALL]) var transactionItems: List<TransactionItem> = ArrayList(),
)


@Entity
class TransactionItem(
    @Column(nullable = false) val count: Long,
    @Column(nullable = false) val amount: BigDecimal,
    @Column(nullable = false) val totalAmount: BigDecimal,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @ManyToOne val product: Product? = null,
    @ManyToOne(fetch = FetchType.LAZY) var transaction: Transaction? = null,
)

