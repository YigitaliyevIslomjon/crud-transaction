package com.transaction

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/user/")
class UserController(private val userService: UserService) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: UserDto): ResponseEntity<User> {
        val result = userService.add(dto)
        return ResponseEntity.ok(result)
    }

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: UserDto): ResponseEntity<Result> {
        val result = userService.edit(id, dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Result> {
        val result = userService.delete(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<User> {
        val result = userService.getOne(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<User>> {
        val result = userService.getAll()
        return ResponseEntity.ok(result)
    }
}


@RestController
@RequestMapping("api/category/")
class CategoryController(private val categoryService: CategoryService) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: CategoryDto): ResponseEntity<Category> {
        val result = categoryService.add(dto)
        return ResponseEntity.ok(result)
    }

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long,@Valid @RequestBody dto: CategoryDto): ResponseEntity<Result> {
        val result = categoryService.edit(id, dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Result> {
        val result = categoryService.delete(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<Category> {
        val result = categoryService.getOne(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<Category>> {
        val result = categoryService.getAll()
        return ResponseEntity.ok(result)
    }
}


@RestController
@RequestMapping("api/user-payment-transaction/")
class UserPaymentTransactionController (private val userPaymentTransactionService: UserPaymentTransactionService) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: UserPaymentTransactionDto): ResponseEntity<UserPaymentTransaction> {
        val result = userPaymentTransactionService.add(dto)
        return ResponseEntity.ok(result)
    }

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: UserPaymentTransactionDto): ResponseEntity<Result> {
        val result = userPaymentTransactionService.edit(id, dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Result> {
        val result = userPaymentTransactionService.delete(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<UserPaymentTransaction> {
        val result = userPaymentTransactionService.getOne(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<UserPaymentTransaction>> {
        val result = userPaymentTransactionService.getAll()
        return ResponseEntity.ok(result)
    }
}


@RestController
@RequestMapping("api/transaction/")
class TransactionController(private val transactionService: TransactionService) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: TransactionDto): ResponseEntity<Transaction> {
        val result = transactionService.add(dto)
        return ResponseEntity.ok(result)
    }

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long,@Valid @RequestBody dto: TransactionDto): ResponseEntity<Result> {
        val result = transactionService.edit(id, dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Result> {
        val result = transactionService.delete(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<Transaction> {
        val result = transactionService.getOne(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<Transaction>> {
        val result = transactionService.getAll()
        return ResponseEntity.ok(result)
    }
}

@RestController
@RequestMapping("api/transaction-item/")
class TransactionItemController (private val transactionItemService: TransactionItemService) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: TransactionItemDto): ResponseEntity<TransactionItem> {
        val result = transactionItemService.add(dto)
        return ResponseEntity.ok(result)
    }

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long,@Valid @RequestBody dto: TransactionItemDto): ResponseEntity<Result> {
        val result = transactionItemService.edit(id, dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Result> {
        val result = transactionItemService.delete(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<TransactionItem> {
        val result = transactionItemService.getOne(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<TransactionItem>> {
        val result = transactionItemService.getAll()
        return ResponseEntity.ok(result)
    }
}

@RestController
@RequestMapping("api/product/")
class ProductController(private val productService: ProductService) {

    @PostMapping("add")
    fun add(@Valid @RequestBody dto: ProductDto): ResponseEntity<Product> {
        val result = productService.add(dto)
        return ResponseEntity.ok(result)
    }

    @PutMapping("{id}")
    fun edit(@PathVariable id: Long, @Valid @RequestBody dto: ProductDto): ResponseEntity<Result> {
        val result = productService.edit(id, dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Result> {
        val result = productService.delete(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<Product> {
        val result = productService.getOne(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<Product>> {
        val result = productService.getAll()
        return ResponseEntity.ok(result)
    }
}