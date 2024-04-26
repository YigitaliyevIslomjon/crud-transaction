package com.transaction

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.util.Date

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @CreatedBy var createdBy: Long? = null,
    @LastModifiedBy var modifiedBy: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)
@Entity
class RefreshToken(
    val token: String,
    val userDetails: UserDetails,
): BaseEntity()

@Entity
class Category(
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var orderValue: Long,
    @Column(nullable = false) var description: String,
) : BaseEntity()

@Entity
class Product(
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var count: Long,
    @ManyToOne var category: Category,
): BaseEntity()

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false) var fullName: String,
    @Column(nullable = false, unique = true) var username: String,
    @Column(nullable = false) var balance: BigDecimal,
    @Column(nullable = false) var password: String,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var role: Role,
): BaseEntity()


@Entity
data class UserPaymentTransaction(
    @ManyToOne val user: User, @Column(nullable = false) val amount: BigDecimal,
    @Column(nullable = false) val date: Date = Date(),
): BaseEntity()

@Entity
class Transaction(
    @ManyToOne(fetch = FetchType.LAZY) val user: User,
    @Column(nullable = false) val totalAmount: BigDecimal,
): BaseEntity()


@Entity
class TransactionItem(
    @Column(nullable = false) val count: Long,
    @Column(nullable = false) val amount: BigDecimal,
    @Column(nullable = false) val totalAmount: BigDecimal,
    @ManyToOne val product: Product? = null,
    @ManyToOne(fetch = FetchType.LAZY) var transaction: Transaction? = null,
): BaseEntity()

