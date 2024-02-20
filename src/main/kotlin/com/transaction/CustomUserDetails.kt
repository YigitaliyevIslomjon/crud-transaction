package com.transaction


import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(user: User): UserDetails {
    private val id : Long  = user.id!!
    private val username : String = user.username
    private val password : String = user.password
    private val authorities: Collection<GrantedAuthority>

    init {
        val auths = ArrayList<GrantedAuthority>()
        for (role in user.roles){
            auths.add(SimpleGrantedAuthority(role.name.name.uppercase()))
        }
        this.authorities = auths
        println(authorities)
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
    fun getId(): Long {
        return id
    }

}