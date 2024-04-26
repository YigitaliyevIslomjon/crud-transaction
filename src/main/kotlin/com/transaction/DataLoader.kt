package com.transaction

import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DataLoader(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val username = "dev"
        val password = passwordEncoder.encode("123")
        userRepository.findUserByUsername(username) ?: run {
            userRepository.save(
                User(
                    fullName = "",
                    username,
                    balance = BigDecimal.ZERO,
                    password,
                    Role.ADMIN
                )
            )
        }
    }
}