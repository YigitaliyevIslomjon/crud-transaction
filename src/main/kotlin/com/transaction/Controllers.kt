package com.transaction

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("sign-in")
    fun signIn(@Valid @RequestBody dto: AuthDto) = authService.signIn(dto)

    @PostMapping("refresh")
    fun refreshAccessToken(@RequestBody dto: RefreshTokenRequestDto) =
        authService.refreshAccessToken(dto)
}

@RestController
@RequestMapping("api/user")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("sign-up")
    fun create(@Valid @RequestBody dto: CreateUserDto) = userService.create(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = userService.getById(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: UserUpdateDto) = userService.update(id, dto)

    @GetMapping("pageable")
    fun getAll() = userService.getAll()
}


@RestController
@RequestMapping("api/category")
class CategoryController(private val categoryService: CategoryService) {
    @PostMapping("add")
    fun create(@Valid @RequestBody dto: CreateCategoryDto) = categoryService.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: UpdateCategoryDto) = categoryService.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = categoryService.delete(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = categoryService.getById(id)

    @GetMapping("pageable")
    fun getAll() = categoryService.getAll()
}

@RestController
@RequestMapping("api/user-payment-transaction")
class UserPaymentTransactionController(private val userPaymentTransactionService: UserPaymentTransactionService) {
    @PostMapping("add")
    fun create(@Valid @RequestBody dto: CreateUserPaymentTransactionDto) = userPaymentTransactionService.create(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = userPaymentTransactionService.delete(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = userPaymentTransactionService.getById(id)

    @GetMapping("pageable")
    fun getAll() = userPaymentTransactionService.getAll()
}

@RestController
@RequestMapping("api/transaction")
class TransactionController(private val transactionService: TransactionService) {
    @PostMapping("add")
    fun create(@Valid @RequestBody dto: TransactionDto) = transactionService.create(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = transactionService.delete(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = transactionService.getById(id)

    @GetMapping("pageable")
    fun getAll() = transactionService.getAll()
}

@RestController
@RequestMapping("api/transaction-item")
class TransactionItemController(private val transactionItemService: TransactionItemService) {
    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = transactionItemService.delete(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = transactionItemService.getById(id)

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("pageable-admin")
    fun getAllAdmin() = transactionItemService.getAll()

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("pageable")
    fun getAllUser() = transactionItemService.getAll()
}

@RestController
@RequestMapping("api/product")
class ProductController(private val productService: ProductService) {
    @PostMapping("add")
    fun create(@Valid @RequestBody dto: CreateProductDto) = productService.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: UpdateProductDto) =
        productService.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = productService.delete(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = productService.getById(id)

    @GetMapping("pageable")
    fun getAll() = productService.getAll()
}