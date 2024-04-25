package com.transaction

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@SpringBootApplication
class TransactionApplication

fun main(args: Array<String>) {
    runApplication<TransactionApplication>(*args)
}
