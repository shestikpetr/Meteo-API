package com.shestikpetr.meteoapi.security

import com.shestikpetr.meteoapi.exception.AuthenticationException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractBearerToken(request)
        if (token != null) authenticate(token)
        filterChain.doFilter(request, response)
    }

    private fun authenticate(token: String) {
        val principal = try {
            jwtService.readPrincipalFromAccessToken(token)
        } catch (_: AuthenticationException) {
            return
        }
        SecurityContextHolder.getContext().authentication = buildAuthentication(principal)
    }

    private fun buildAuthentication(principal: UserPrincipal): UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
        principal,
        null,
        listOf(SimpleGrantedAuthority(ROLE_PREFIX + principal.role.value.uppercase())),
    )

    private fun extractBearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        if (!header.startsWith(BEARER_PREFIX)) return null
        return header.substring(BEARER_PREFIX.length).trim().takeIf { it.isNotEmpty() }
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val ROLE_PREFIX = "ROLE_"
    }
}
