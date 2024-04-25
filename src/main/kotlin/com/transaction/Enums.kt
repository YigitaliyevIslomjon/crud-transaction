package com.transaction

enum class Role {
    USER,
    ADMIN,
    SUPER_ADMIN
}
enum class ErrorCode(val code: Int) {
    USERNAME_EXIST(100),
    USER_NOT_FOUND(101),
    CATEGORY_NOT_FOUND(102),
    PRODUCT_NOT_FOUND(103),
    USER_PAYMENT_TRANSACTION_NOT_FOUND(104),
    TRANSACTION_NOT_FOUND(105),
    TRANSACTION_ITEM_NOT_FOUND(106),
    NOT_ENOUGH_MONEY(107)
}
