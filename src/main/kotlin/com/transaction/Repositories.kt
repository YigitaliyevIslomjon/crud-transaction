package com.transaction

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun trash(id: Long): T
    fun trashList(ids: List<Long>): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>
    fun findAllNotDeleted(): List<T>
    fun findByIdNotDeleted(id: Long): T?
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {
    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }
    override fun trash(id: Long) = save(findById(id).get().apply { deleted = true })
    override fun findAllNotDeleted(pageable: Pageable) = findAll(isNotDeletedSpecification, pageable)
    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findByIdNotDeleted(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }
    override fun trashList(ids: List<Long>): List<T> = ids.map { trash(it) }
}
interface CategoryRepository : BaseRepository<Category> {
}

interface ProductRepository : BaseRepository<Product> {}
interface UserRepository : BaseRepository<User> {
    fun existsByUsername(username: String): Boolean
    fun findUserByUsername(username: String): User?
}

interface UserPaymentTransactionRepository : BaseRepository<UserPaymentTransaction> {
    @Query(
        value = "SELECT upt.* FROM user_payment_transaction upt INNER JOIN users u ON upt.user_id = u.id WHERE u.username = :username and upt.deleted = false",
        nativeQuery = true
    )
    fun getAllUserPaymentTransactionByUserName(username: String): List<UserPaymentTransaction>
}

interface TransactionRepository : BaseRepository<Transaction> {
}

interface TransactionItemRepository : BaseRepository<TransactionItem> {
    @Query(
        value = "SELECT ti.* FROM transaction_item ti " +
                "INNER JOIN transaction t ON ti.transaction_id = t.id " +
                "INNER JOIN users u ON t.user_id = u.id " +
                "WHERE u.username = :username",
        nativeQuery = true
    )
    fun findAllByUsername(username: String): List<TransactionItem>
}


interface RefreshTokenRepository : BaseRepository<RefreshToken> {
    fun findByToken(token: String): RefreshToken?
}