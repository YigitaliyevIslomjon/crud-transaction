package com.transaction

import jakarta.validation.Valid
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
    fun authentication(@Valid @RequestBody dto: AuthDto): AuthDtoResponse = authService.authentication(dto)

    @PostMapping("refresh")
    fun refreshAccessToken(@RequestBody dto: RefreshTokenRequestDto): RefreshTokenResponseDto =
        authService.refreshAccessToken(dto)
}

@RestController
@RequestMapping("api/user")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("sign-up")
    fun add(@Valid @RequestBody dto: UserDto) = userService.add(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = userService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): UserDtoResponse = userService.getOne(id)

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: UserEditDto): Result = userService.edit(id, dto)

    @GetMapping("pageable")
    fun getAll(): List<UserDtoResponse> = userService.getAll()
}

@RestController
@RequestMapping("api/role")
class RoleController(
    private var roleService: RoleService,
) {
    @PostMapping("add")
    fun add(@RequestBody dto: RoleDto): Result = roleService.add(dto)

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: RoleDto): Result = roleService.edit(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = roleService.delete(id)

    @GetMapping("pageable")
    fun getAll(): List<RoleDtoResponse> = roleService.getAll()

}

@RestController
@RequestMapping("api/category")
class CategoryController(private val categoryService: CategoryService) {
    @PostMapping("add")
    fun add(@Valid @RequestBody dto: CategoryDto): Result = categoryService.add(dto)

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: CategoryDto): Result = categoryService.edit(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = categoryService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): CategoryDtoResponse = categoryService.getOne(id)

    @GetMapping("pageable")
    fun getAll(): List<CategoryDtoResponse> = categoryService.getAll()
}

@RestController
@RequestMapping("api/user-payment-transaction/")
class UserPaymentTransactionController(private val userPaymentTransactionService: UserPaymentTransactionService) {
    @PostMapping("add")
    fun add(@Valid @RequestBody dto: UserPaymentTransactionDto): Result = userPaymentTransactionService.add(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = userPaymentTransactionService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): UserPaymentTransactionDtoResponse = userPaymentTransactionService.getOne(id)

    @GetMapping("pageable")
    fun getAll(): List<UserPaymentTransactionDtoResponse> = userPaymentTransactionService.getAll()
}

@RestController
@RequestMapping("api/transaction/")
class TransactionController(private val transactionService: TransactionService) {
    @PostMapping("add")
    fun add(@Valid @RequestBody dto: TransactionDto): Result = transactionService.add(dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = transactionService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): TransactionDtoResponse = transactionService.getOne(id)

    @GetMapping("pageable")
    fun getAll(): List<TransactionDtoResponse> = transactionService.getAll()
}

@RestController
@RequestMapping("api/transaction-item/")
class TransactionItemController(private val transactionItemService: TransactionItemService) {
    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = transactionItemService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): TransactionItemDtoResponse = transactionItemService.getOne(id)

    @GetMapping("pageable")
    fun getAll(): List<TransactionItemDtoResponse> = transactionItemService.getAll()
}

@RestController
@RequestMapping("api/product/")
class ProductController(private val productService: ProductService) {
    @PostMapping("add")
    fun add(@Valid @RequestBody dto: ProductDto): Result = productService.add(dto)

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: ProductDto): Result =
        productService.edit(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): Result = productService.delete(id)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ProductDtoResponse = productService.getOne(id)

    @GetMapping("pageable")
    fun getAll(): List<ProductDtoResponse> = productService.getAll()
}