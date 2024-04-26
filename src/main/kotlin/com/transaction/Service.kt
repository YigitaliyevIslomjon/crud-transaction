package com.transaction

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    fun create(dto: CreateUserDto): GetUserDto
    fun delete(id: Long)
    fun update(id: Long, dto: UserUpdateDto): GetUserDto
    fun getById(id: Long): GetUserDto
    fun getAll(): List<GetUserDto>
}

interface AuthService {
    fun signIn(dto: SignInDto): AuthDtoResponse
    fun refreshAccessToken(dto: RefreshTokenRequestDto): RefreshTokenResponseDto
}


interface CategoryService {
    fun create(dto: CreateCategoryDto): GetCategoryDto
    fun update(id: Long, dto: UpdateCategoryDto): GetCategoryDto
    fun delete(id: Long)
    fun getById(id: Long): GetCategoryDto
    fun getAll(): List<GetCategoryDto>
}

interface ProductService {
    fun create(dto: CreateProductDto): GetProductDto
    fun update(id: Long, dto: UpdateProductDto): GetProductDto
    fun delete(id: Long)
    fun getById(id: Long): GetProductDto
    fun getAll(): List<GetProductDto>
}

interface UserPaymentTransactionService {
    fun create(dto: CreateUserPaymentTransactionDto): GetUserPaymentTransactionDto
    fun delete(id: Long)
    fun getById(id: Long): GetUserPaymentTransactionDto
    fun getAll(): List<GetUserPaymentTransactionDto>
}

interface TransactionService {
    fun create(dto: TransactionDto): GetTransactionDto
    fun delete(id: Long)
    fun getById(id: Long): GetTransactionDto
    fun getAll(pageable: Pageable): Page<GetTransactionDto>
}

interface TransactionItemService {
    fun delete(id: Long)
    fun getById(id: Long): GetTransactionItemDto
    fun getAll(): List<GetTransactionItemDto>
}

@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
) : UserService {
    override fun create(dto: CreateUserDto) = dto.run {
        if (userRepository.existsByUsername(username))
            throw UsernameExistException("username $username  is already exist")

        val password = passwordEncoder.encode(dto.password)
        GetUserDto.toResponse(userRepository.save(dto.toEntity(password)))
    }

    override fun update(id: Long, dto: UserUpdateDto): GetUserDto = dto.run {
        val user = userRepository.findByIdNotDeleted(id)
            ?: throw UserNotFoundException("userId $id is not found")

        username?.let {
            if (user.username != it && userRepository.existsByUsername(it))
                throw UsernameExistException("username $username  is already exist")
            user.username = it
        }

        fullName?.let {
            user.fullName = it
        }
        password?.let {
            val password = passwordEncoder.encode(it)
            user.password = password
        }
        role?.let {
            user.role = role
        }
        GetUserDto.toResponse(userRepository.save(user))
    }

    override fun delete(id: Long) {
        userRepository.findByIdNotDeleted(id)
            ?: throw UserNotFoundException(" userid $id is not found")
        userRepository.trash(id)
    }

    override fun getById(id: Long): GetUserDto {
        val user = userRepository.findByIdNotDeleted(id)
            ?: throw UserNotFoundException("id $id is not found")
        return GetUserDto.toResponse(user)
    }

    override fun getAll(): List<GetUserDto> =
        userRepository.findAllNotDeleted().map(GetUserDto.Companion::toResponse)
}

@Service
class AuthServiceImpl(
    private val jwtService: JwtService,
    private var authenticationManager: AuthenticationManager,
    private var userDetailsService: UserDetailsService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) : AuthService {
    override fun signIn(@RequestBody dto: SignInDto): AuthDtoResponse {

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
class JwtService(
    val jwtProperties: JwtProperties
) {
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
    override fun create(dto: CreateCategoryDto): GetCategoryDto =
        GetCategoryDto.toResponse(categoryRepository.save(dto.toEntity()))

    override fun update(id: Long, dto: UpdateCategoryDto): GetCategoryDto = dto.run {
        val category = categoryRepository.findByIdNotDeleted(id)
            ?: throw CategoryNotFoundException("category id $id is not found")
        name?.let {
            category.name = name
        }
        orderValue?.let {
            category.orderValue = orderValue
        }
        description?.let {
            category.description = description
        }
        GetCategoryDto.toResponse(categoryRepository.save(category))
    }

    override fun delete(id: Long) {
        categoryRepository.findByIdNotDeleted(id)
            ?: throw CategoryNotFoundException("category id $id is not found")
        categoryRepository.trash(id)
    }

    override fun getById(id: Long): GetCategoryDto {
        val category = categoryRepository.findByIdNotDeleted(id)
            ?: throw CategoryNotFoundException("category id $id is not found")
        return GetCategoryDto.toResponse(category)
    }

    override fun getAll(): List<GetCategoryDto> {
        return categoryRepository.findAllNotDeleted().map(GetCategoryDto.Companion::toResponse)
    }
}

@Service
class ProductServiceImpl(
    val categoryRepository: CategoryRepository,
    val productRepository: ProductRepository,
) : ProductService {
    override fun create(dto: CreateProductDto): GetProductDto {
        val category = categoryRepository.findByIdNotDeleted(dto.categoryId)
            ?: throw ProductNotFoundException("${dto.categoryId} is not found")
        val product = productRepository.save(dto.toEntity(category))
        return GetProductDto.toResponse(product)
    }

    override fun update(id: Long, dto: UpdateProductDto): GetProductDto = dto.run {

        val product =
            productRepository.findByIdNotDeleted(id)
                ?: throw ProductNotFoundException("product id $id is not found")

        name?.let {
            product.name = name
        }

        categoryId?.let {
            val category = categoryRepository.findByIdNotDeleted(categoryId)
                ?: throw CategoryNotFoundException("category id $categoryId is not found")
            product.category = category
        }
        count?.let {
            product.count = count
        }

        GetProductDto.toResponse(productRepository.save(product))
    }

    override fun delete(id: Long) {
        productRepository.findByIdNotDeleted(id)
            ?: throw ProductNotFoundException("productId $id is not found")
        productRepository.trash(id)
    }

    override fun getById(id: Long): GetProductDto {
        val product = productRepository.findByIdNotDeleted(id)
            ?: throw ProductNotFoundException("productId $id is not found")
        return GetProductDto.toResponse(product)
    }

    override fun getAll(): List<GetProductDto> {
        return productRepository.findAllNotDeleted().map(GetProductDto.Companion::toResponse)
    }
}

@Service
class TransactionServiceImpl(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val productRepository: ProductRepository
) : TransactionService {

    @Transactional
    override fun create(dto: TransactionDto) = dto.run {
        val user = userRepository.findByIdNotDeleted(dto.userId)
            ?: throw UserNotFoundException("userId ${dto.userId} is not found")

        val transactionItems = mutableListOf<TransactionItem>()
        var totalAmountTransaction = BigDecimal.ZERO

        dto.items.forEach { item ->
            val product = productRepository.findByIdNotDeleted(item.productId)
                ?: throw ProductNotFoundException("productId ${item.productId} is not found")

            val totalAmount = item.count.toBigDecimal() * item.amount
            totalAmountTransaction += totalAmount

            val transactionItem = TransactionItem(
                count = item.count,
                amount = item.amount,
                totalAmount = totalAmount,
                product = product
            )
            transactionItems.add(transactionItem)
        }

        var transaction = Transaction(
            user = user,
            totalAmount = totalAmountTransaction,
        )

        transactionItems.forEach { it.transaction = transaction }

        transactionItemRepository.saveAll(transactionItems)
        transaction = transactionRepository.save(transaction)

        if (user.balance >= transaction.totalAmount) {
            user.balance -= transaction.totalAmount
        } else {
            throw NotEnoughMoneyException("you don't have enough money, your balance is ${user.balance}, totalAmount is ${transaction.totalAmount}")
        }
        userRepository.save(user)
        GetTransactionDto.toResponse(transaction)
    }

    override fun delete(id: Long) {
        transactionRepository.findByIdNotDeleted(id)
            ?: throw TransactionNotFoundException("transactionId $id is not found")
        transactionRepository.trash(id)
    }

    override fun getById(id: Long): GetTransactionDto {
        val transaction = transactionRepository.findByIdNotDeleted(id)
            ?: throw TransactionNotFoundException("transactionId $id is not found")

        return GetTransactionDto.toResponse(transaction)
    }

    override fun getAll(pageable: Pageable): Page<GetTransactionDto> {
        return transactionRepository.findAllNotDeleted(pageable).map(GetTransactionDto.Companion::toResponse)
    }
}

@Service
class TransactionItemServiceImpl(
    val transactionItemRepository: TransactionItemRepository,
) : TransactionItemService {

    override fun delete(id: Long) {
        transactionItemRepository.findByIdNotDeleted(id)
            ?: throw TransactionItemNotFoundException("transactionItemId $id is not found")
        transactionItemRepository.trash(id)
    }

    override fun getById(id: Long): GetTransactionItemDto {
        val transactionItem = transactionItemRepository.findByIdNotDeleted(id)
            ?: throw TransactionItemNotFoundException("transactionItemId $id is not found")
        return GetTransactionItemDto.toResponse(transactionItem)
    }

    override fun getAll(): List<GetTransactionItemDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.authorities.forEach {
            if (it.authority == "ADMIN") {
                return transactionItemRepository.findAllNotDeleted().map(GetTransactionItemDto.Companion::toResponse)
            }

            if (it.authority == "USER") {
                return transactionItemRepository.findAllByUsername(authentication.name)
                    .map(GetTransactionItemDto.Companion::toResponse)
            }
        }
        throw UserAuthorizationFailureException("user can not access")
    }

}

@Service
class UserPaymentTransactionServiceImpl(
    val userRepository: UserRepository,
    val userPaymentTransactionRepository: UserPaymentTransactionRepository
) : UserPaymentTransactionService {
    override fun create(dto: CreateUserPaymentTransactionDto) = dto.run {
        var user = userRepository.findByIdNotDeleted(dto.userId)
            ?: throw UserNotFoundException("userId ${dto.userId} is not found")
        user.balance += dto.amount
        user = userRepository.save(user)

        val userPaymentTransaction =
            userPaymentTransactionRepository.save(dto.toEntity(user))
        GetUserPaymentTransactionDto.toResponse(userPaymentTransaction)
    }

    override fun delete(id: Long) {
        userPaymentTransactionRepository.findByIdNotDeleted(id)
            ?: throw UserPaymentTransactionNotFoundException("userPaymentTransactionId $id is not found")
        userPaymentTransactionRepository.trash(id)
    }

    override fun getById(id: Long): GetUserPaymentTransactionDto {
        val userPaymentTransaction = userPaymentTransactionRepository.findByIdNotDeleted(id)
            ?: throw UserPaymentTransactionNotFoundException("userPaymentTransactionId $id is not found")

        return GetUserPaymentTransactionDto.toResponse(userPaymentTransaction)
    }

    override fun getAll(): List<GetUserPaymentTransactionDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        println(authentication.name)
        authentication.authorities.forEach {
            if (it.authority == "ADMIN") {
                return userPaymentTransactionRepository.findAllNotDeleted()
                    .map(GetUserPaymentTransactionDto.Companion::toResponse)
            }

            if (it.authority == "USER") {
                return userPaymentTransactionRepository.getAllUserPaymentTransactionByUserName(authentication.name)
                    .map(GetUserPaymentTransactionDto.Companion::toResponse)
            }
        }
        throw UserAuthorizationFailureException("user can't access this data")
    }
}

