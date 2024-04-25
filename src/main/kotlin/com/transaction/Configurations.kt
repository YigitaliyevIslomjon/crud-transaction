package com.transaction

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationProvider: AuthenticationProvider,
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthFilter: JwtAuthFilter
    ): DefaultSecurityFilterChain {
        http.csrf { it.disable() }.authorizeHttpRequests {
            it.requestMatchers("api/auth/sign-in", "api/user/sign-up", "api/auth/refresh")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "api/role/add").permitAll()
                .requestMatchers(
                    HttpMethod.POST,
                    "api/user-payment-transaction/add", "api/user/id",
                )
                .hasAuthority("USER")
                .requestMatchers(
                    HttpMethod.PUT,
                    "api/user/{id}",
                )
                .hasAuthority("USER")
                .requestMatchers(
                    HttpMethod.GET,
                    "api/user-payment-transaction/pageable", "api/transaction-item/pageable"
                )
                .hasAuthority("USER")
                .requestMatchers(
                    "api/category/*",
                    "api/user-payment-transaction/*",
                    "api/transaction-item/*",
                    "api/transaction/*",
                    "api/product/*",
                    "api/role/*",
                    "api/user/*"
                ).hasAnyAuthority("ADMIN")
                .anyRequest().authenticated()
        }.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }.authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { it.accessDeniedHandler(accessDeniedHandler()) }
        return http.build()
    }

    private fun accessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException ->
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = "application/json"
            response.writer.write("{'message': 'Access denied: ${accessDeniedException.message}'}")
        }
    }
}

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configurations {
    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService {
        return UserDetailsServiceImpl(userRepository)
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService(userRepository))
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        return authenticationProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.getAuthenticationManager()
    }
}

@ConfigurationProperties("jwt")
data class JwtProperties(
    val key: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)


