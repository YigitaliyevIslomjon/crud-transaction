package com.transaction

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.util.*
import javax.crypto.SecretKey

interface UserService {
    fun add(dto: UserDto): Result
    fun edit(id: Long, dto: UserEditDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): UserDtoResponse
    fun getAll(): List<UserDtoResponse>
}

interface AuthService {
    fun authentication(dto: AuthDto): AuthDtoResponse
    fun refreshAccessToken(dto: RefreshTokenRequestDto): RefreshTokenResponseDto
}

interface RoleService {
    fun add(dto: RoleDto): Result
    fun edit(id: Long, dto: RoleDto): Result
    fun delete(id: Long): Result
    fun getAll(): List<RoleDtoResponse>
}

interface CategoryService {
    fun add(dto: CategoryDto): Result
    fun edit(id: Long, dto: CategoryDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): CategoryDtoResponse
    fun getAll(): List<CategoryDtoResponse>
}

interface ProductService {
    fun add(dto: ProductDto): Result
    fun edit(id: Long, dto: ProductDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): ProductDtoResponse
    fun getAll(): List<ProductDtoResponse>
}

interface UserPaymentTransactionService {
    fun add(dto: UserPaymentTransactionDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): UserPaymentTransactionDtoResponse
    fun getAll(): List<UserPaymentTransactionDtoResponse>
}

interface TransactionService {
    fun add(dto: TransactionDto): Result
    fun delete(id: Long): Result
    fun getOne(id: Long): TransactionDtoResponse
    fun getAll(): List<TransactionDtoResponse>
}

interface TransactionItemService {
    fun delete(id: Long): Result
    fun getOne(id: Long): TransactionItemDtoResponse
    fun getAll(): List<TransactionItemDtoResponse>
}

@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private var roleRepository: RoleRepository,
    private val userRepository: UserRepository
) : UserService {
    override fun add(dto: UserDto) = dto.run {
        userRepository.findUserByUsername(username)?.let {
            throw IllegalStateException("username $username  is already exist")
        }
        val password = passwordEncoder.encode(dto.password)
        val rolesList = mutableListOf<Role>()
        roles.forEach {
            val role = roleRepository.findByIdOrNull(it) ?: throw EntityNotFoundException("id $it is not found")
            rolesList.add(role)
        }

        UserDtoResponse.toResponse(
            userRepository.save(
                User(
                    fullName,
                    username,
                    balance = BigDecimal.ZERO,
                    password,
                    rolesList.toHashSet()
                )
            )
        )
        Result(message = "data are saved successfully")
    }

    override fun edit(id: Long, dto: UserEditDto) = dto.run {
        val existingUser = userRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        /*
                userRepository.findUserByUsername(dto.username)
                    ?: throw IllegalStateException("username is already exist")*/
        val rolesList = mutableListOf<Role>()
        roles.forEach {
            val role = roleRepository.findByIdOrNull(it) ?: throw EntityNotFoundException("id $it is not found")
            rolesList.add(role)
        }
        val password = passwordEncoder.encode(dto.password)
        val updatedUser = User(
            fullName,
            username = existingUser.username,
            balance = existingUser.balance,
            password,
            rolesList.toHashSet(),
            existingUser.id
        )
        userRepository.save(updatedUser)
        Result(message = "data are edited successfully")
    }

    override fun delete(id: Long): Result {
        userRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(" userid $id is not found")

        userRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getOne(id: Long): UserDtoResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        return UserDtoResponse.toResponse(user)
    }

    override fun getAll(): List<UserDtoResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        println(authentication.authorities)
        return userRepository.findAll().map { UserDtoResponse.toResponse(it) }
    }
}

@Service
class AuthServiceImpl(
    private val jwtService: JwtService,
    private var authenticationManager: AuthenticationManager,
    private var userDetailsService: UserDetailsService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) : AuthService {
    override fun authentication(@RequestBody dto: AuthDto): AuthDtoResponse {

        val user = userDetailsService.loadUserByUsername(dto.username)
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                dto.username,
                dto.password,
                user.authorities
            )
        )
        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user)

        refreshTokenRepository.save(RefreshToken(refreshToken, user))

        return AuthDtoResponse(
            accessToken,
            refreshToken
        )
    }

    override fun refreshAccessToken(dto: RefreshTokenRequestDto): RefreshTokenResponseDto {
        val extractedUsername = jwtService.extractUsername(dto.token)
        return extractedUsername.let { username ->
            val currentUserDetails = userDetailsService.loadUserByUsername(username)
            val refreshTokenUserDetails = refreshTokenRepository.findByToken(dto.token)
            println(refreshTokenUserDetails)
            println(refreshTokenUserDetails?.userDetails)
            if (!jwtService.isTokenExpired(dto.token) && refreshTokenUserDetails?.userDetails?.username == currentUserDetails.username)
                RefreshTokenResponseDto(createAccessToken(currentUserDetails))
            else
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token.")
        }
    }

    private fun createAccessToken(user: UserDetails) = jwtService.generateToken(
        user,
        getAccessTokenExpiration()
    )

    private fun createRefreshToken(user: UserDetails) = jwtService.generateToken(
        user,
        getRefreshTokenExpiration()
    )

    private fun getAccessTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)

    private fun getRefreshTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
}


@Service
class RoleServiceImpl(
    val roleRepository: RoleRepository,
) : RoleService {
    override fun add(dto: RoleDto) = dto.run {
        roleRepository.save(Role(name))
        Result(message = "data are saved successfully")
    }

    override fun edit(id: Long, dto: RoleDto) = dto.run {
        val existingRole = roleRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        val updatedRole = Role(name, existingRole.id)
        roleRepository.save(updatedRole)
        Result(message = "data are edited successfully")
    }

    override fun delete(id: Long): Result {
        roleRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        roleRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getAll(): List<RoleDtoResponse> {
        return roleRepository.findAll().map { RoleDtoResponse.toResponse(it) }
    }
}

@Service
class JwtService(
    val jwtProperties: JwtProperties
) {
    companion object {
        const val SECRET = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629"
    }

    fun secretKey(): SecretKey {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.key))
    }

    fun generateToken(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String = Jwts.builder()
        .claims().subject(userDetails.username)
        .issuedAt(Date(System.currentTimeMillis())).expiration(expirationDate).add(additionalClaims).and()
        .signWith(secretKey()).compact()

    fun extractAllClaims(token: String): Claims =
        Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(token).payload

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T =
        claimsResolver(extractAllClaims(token))

    fun extractUsername(token: String): String = extractClaim(token, Claims::getSubject)
    fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)
    fun isTokenExpired(token: String) = extractExpiration(token).before(Date())
    fun validatedToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}


@Service
class CategoryServiceImpl(
    val categoryRepository: CategoryRepository,
) : CategoryService {
    override fun add(dto: CategoryDto) = dto.run {
        categoryRepository.save(Category(name, orderValue, description))
        Result(message = "data are saved successfully")
    }

    override fun edit(id: Long, dto: CategoryDto) = dto.run {
        val existingCategory = categoryRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        val updatedCategory = Category(name, orderValue, description, existingCategory.id)
        categoryRepository.save(updatedCategory)
        Result(message = "data are edited successfully")
    }

    override fun delete(id: Long): Result {
        categoryRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        categoryRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getOne(id: Long): CategoryDtoResponse {
        val category = categoryRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        return CategoryDtoResponse.toResponse(category)
    }

    override fun getAll(): List<CategoryDtoResponse> {
        return categoryRepository.findAll().map { CategoryDtoResponse.toResponse(it) }
    }
}

@Service
class ProductServiceImpl(
    val categoryRepository: CategoryRepository,
    val productRepository: ProductRepository,
) : ProductService {
    override fun add(dto: ProductDto) = dto.run {
        val existingCategory = categoryRepository.findByIdOrNull(dto.categoryId)
            ?: throw EntityNotFoundException("${dto.categoryId} is not found")

        productRepository.save(
            Product(name, count, existingCategory)
        )
        Result(message = "data are saved successfully")
    }

    override fun edit(id: Long, dto: ProductDto) = dto.run {
        val existingCategory = categoryRepository.findByIdOrNull(dto.categoryId)
            ?: throw EntityNotFoundException("${dto.categoryId} is not found")

        val existingProduct =
            productRepository.findByIdOrNull(id)
                ?: throw EntityNotFoundException("id $id is not found")

        val updatedProduct = Product(
            name, count, existingCategory, existingProduct.id
        )
        productRepository.save(updatedProduct)
        Result(message = "data are edited successfully")
    }

    override fun delete(id: Long): Result {
        productRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")

        productRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getOne(id: Long): ProductDtoResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")

        return ProductDtoResponse.toResponse(product)
    }

    override fun getAll(): List<ProductDtoResponse> {
        return productRepository.findAll().map { ProductDtoResponse.toResponse(it) }
    }
}

@Service
class TransactionServiceImpl(
    val userRepository: UserRepository,
    val transactionRepository: TransactionRepository,
    val productRepository: ProductRepository
) : TransactionService {

    @Transactional
    override fun add(dto: TransactionDto) = dto.run {
        val existingUser = userRepository.findByIdOrNull(dto.userId)
            ?: throw EntityNotFoundException("${dto.userId} is not found")

        val transactionItems = mutableListOf<TransactionItem>()
        var totalAmountTransaction = BigDecimal.ZERO

        dto.items.forEach { itemDto ->
            val product = productRepository.findByIdOrNull(itemDto.productId)
                ?: throw EntityNotFoundException("${itemDto.productId} is not found")

            val totalAmount = itemDto.count.toBigDecimal() * itemDto.amount
            totalAmountTransaction += totalAmount

            val transactionItem = TransactionItem(
                count = itemDto.count,
                amount = itemDto.amount,
                totalAmount = totalAmount,
                product = product
            )
            transactionItems.add(transactionItem)
        }

        val transaction = Transaction(
            user = existingUser,
            totalAmount = totalAmountTransaction,
            date = Date()
        )

        transactionItems.forEach { it.transaction = transaction }
        transaction.transactionItems = transactionItems
        val savedTransaction = transactionRepository.save(transaction)

        if (existingUser.balance >= savedTransaction.totalAmount) {
            existingUser.balance -= savedTransaction.totalAmount
        } else {
            throw IllegalStateException("you don't have enough money, your balance is ${existingUser.balance}, totalAmount is ${savedTransaction.totalAmount}")
        }
        Result(message = "data are saved successfully")
    }

    override fun delete(id: Long): Result {
        transactionRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")

        transactionRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getOne(id: Long): TransactionDtoResponse {
        val transaction = transactionRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")

        return TransactionDtoResponse.toResponse(transaction)
    }

    override fun getAll(): List<TransactionDtoResponse> {
        return transactionRepository.findAll().map { TransactionDtoResponse.toResponse(it) }
    }
}

@Service
class TransactionItemServiceImpl(
    val transactionItemRepository: TransactionItemRepository,
) : TransactionItemService {

    override fun delete(id: Long): Result {
        transactionItemRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")

        transactionItemRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getOne(id: Long): TransactionItemDtoResponse {
        val existingTransactionItem = transactionItemRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException("id $id is not found")
        return TransactionItemDtoResponse.toResponse(existingTransactionItem)
    }

    override fun getAll(): List<TransactionItemDtoResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        println(authentication.name)
        authentication.authorities.forEach{
            if(it.authority == "ADMIN") {
                return transactionItemRepository.findAll().map { TransactionItemDtoResponse.toResponse(it) }
            }

            if (it.authority == "USER") {
                return transactionItemRepository.findAllByUsername(authentication.name).map { t-> TransactionItemDtoResponse.toResponse(t) }
            }

        }
        throw IllegalStateException("user cann't access")
    }
}

@Service
class UserPaymentTransactionServiceImpl(
    val userRepository: UserRepository,
    val userPaymentTransactionRepository: UserPaymentTransactionRepository
) : UserPaymentTransactionService {
    override fun add(dto: UserPaymentTransactionDto) = dto.run {
        val existingUser = userRepository.findByIdOrNull(dto.userId)
            ?: throw EntityNotFoundException("userId ${dto.userId} is not found")
        existingUser.balance += dto.amount
        existingUser.userPaymentTransactions =
            arrayListOf((UserPaymentTransaction(existingUser, amount, date = Date())))
        userRepository.save(existingUser)
        Result(message = "data are saved successfully")
    }

    override fun delete(id: Long): Result {
        userPaymentTransactionRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("id $id is not found")
        userPaymentTransactionRepository.deleteById(id)
        return Result(message = "data are deleted successfully")
    }

    override fun getOne(id: Long): UserPaymentTransactionDtoResponse {
        val existingUserPaymentTransaction = userPaymentTransactionRepository.findById(id).orElseThrow {
            throw EntityNotFoundException("id $id is not found")
        }
        return UserPaymentTransactionDtoResponse.toResponse(existingUserPaymentTransaction)
    }

    override fun getAll(): List<UserPaymentTransactionDtoResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        println(authentication.name)
        authentication.authorities.forEach{
            if(it.authority  == "ADMIN") {
                return userPaymentTransactionRepository.findAll()
                    .map { t-> UserPaymentTransactionDtoResponse.toResponse(t) }
            }

            if (it.authority  == "USER") {
                return userPaymentTransactionRepository.getAllUserPaymentTransactionByUserName(authentication.name).map { t-> UserPaymentTransactionDtoResponse.toResponse(t) }
            }

        }
        throw IllegalStateException("user cann't access")
    }
}

