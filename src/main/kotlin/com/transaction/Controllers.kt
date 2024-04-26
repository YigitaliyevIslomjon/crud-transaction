package com.transaction

import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val API_PREFIX = "api/v1"

@RestController
@RequestMapping("$API_PREFIX/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("sign-in")
    fun signIn(@Valid @RequestBody dto: SignInDto) = authService.signIn(dto)

    @PostMapping("refresh")
    fun refreshAccessToken(@RequestBody dto: RefreshTokenRequestDto) =
        authService.refreshAccessToken(dto)
}

@RestController
@RequestMapping("$API_PREFIX/user")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun create(@Valid @RequestBody dto: CreateUserDto) = userService.create(dto)

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getById(@PathVariable id: Long) = userService.getById(id)

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: UserUpdateDto) = userService.update(id, dto)

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getAll() = userService.getAll()
}


@RestController
@RequestMapping("$API_PREFIX/category")
class CategoryController(private val categoryService: CategoryService) {
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun create(@Valid @RequestBody dto: CreateCategoryDto) = categoryService.create(dto)

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: UpdateCategoryDto) = categoryService.update(id, dto)

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun delete(@PathVariable id: Long) = categoryService.delete(id)

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getById(@PathVariable id: Long) = categoryService.getById(id)

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getAll() = categoryService.getAll()
}

@RestController
@RequestMapping("$API_PREFIX/user-payment-transaction")
class UserPaymentTransactionController(private val userPaymentTransactionService: UserPaymentTransactionService) {
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('USER')")
    fun create(@Valid @RequestBody dto: CreateUserPaymentTransactionDto) = userPaymentTransactionService.create(dto)


    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun delete(@PathVariable id: Long) = userPaymentTransactionService.delete(id)

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getById(@PathVariable id: Long) = userPaymentTransactionService.getById(id)

    @GetMapping("")
    fun getAll() = userPaymentTransactionService.getAll()
}

@RestController
@RequestMapping("$API_PREFIX/transaction")
class TransactionController(private val transactionService: TransactionService) {
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun create(@Valid @RequestBody dto: TransactionDto) = transactionService.create(dto)

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun delete(@PathVariable id: Long) = transactionService.delete(id)

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getById(@PathVariable id: Long) = transactionService.getById(id)

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getAll(pageable: Pageable) = transactionService.getAll(pageable)
}

@RestController
@RequestMapping("$API_PREFIX/transaction-item")
class TransactionItemController(private val transactionItemService: TransactionItemService) {
    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun delete(@PathVariable id: Long) = transactionItemService.delete(id)

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getById(@PathVariable id: Long) = transactionItemService.getById(id)

    @GetMapping("")
    fun getAll() = transactionItemService.getAll()
}

@RestController
@RequestMapping("$API_PREFIX/product")
class ProductController(private val productService: ProductService) {
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun create(@Valid @RequestBody dto: CreateProductDto) = productService.create(dto)

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: UpdateProductDto) =
        productService.update(id, dto)

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun delete(@PathVariable id: Long) = productService.delete(id)

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getById(@PathVariable id: Long) = productService.getById(id)

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getAll() = productService.getAll()
}