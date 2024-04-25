package com.transaction

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsServiceImpl: UserDetailsServiceImpl
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }
        val jwtToken = authHeader!!.extractTokenValue()
        val email = jwtService.extractUsername(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsServiceImpl.loadUserByUsername(email)
            if (jwtService.validatedToken(jwtToken, foundUser)) {
                updateContent(foundUser, request)
            }
        }
        filterChain.doFilter(request, response)

    }

    private fun String?.doesNotContainBearerToken() = this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() = this.substringAfter("Bearer ")

    private fun updateContent(foundUser: UserDetails, request: HttpServletRequest) {
        
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}


