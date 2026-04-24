package com.shestikpetr.meteoapi.security

import com.shestikpetr.meteoapi.config.ApiRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        configureCommon(http)
        configureAuthorization(http)
        configureJwt(http)
        return http.build()
    }

    private fun configureCommon(http: HttpSecurity) {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
    }

    private fun configureAuthorization(http: HttpSecurity) {
        http.authorizeHttpRequests { auth ->
            auth
                .requestMatchers(*PUBLIC_PATHS).permitAll()
                .anyRequest().authenticated()
        }
    }

    private fun configureJwt(http: HttpSecurity) {
        http
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    private companion object {
        val PUBLIC_PATHS = arrayOf(
            "${ApiRoutes.AUTH}/**",
            "/health",
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
        )
    }
}
