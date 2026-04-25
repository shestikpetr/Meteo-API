package com.shestikpetr.meteoapi.admin.config

import com.shestikpetr.meteoapi.admin.AdminRoutes
import com.shestikpetr.meteoapi.admin.security.AdminUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

// Цепочка безопасности для SSR-админки
// Form-login + session-cookie, hasRole('ADMIN'), CSRF включён (формы Thymeleaf несут токен)
@Configuration
class AdminSecurityConfig {

    @Bean
    @Order(1)
    fun adminSecurityFilterChain(
        http: HttpSecurity,
        adminUserDetailsService: AdminUserDetailsService,
    ): SecurityFilterChain {
        http.securityMatcher(AdminRoutes.BASE, "${AdminRoutes.BASE}/**")
        http.userDetailsService(adminUserDetailsService)
        configureSession(http)
        configureAuthorization(http)
        configureFormLogin(http)
        return http.build()
    }

    private fun configureSession(http: HttpSecurity) {
        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
    }

    private fun configureAuthorization(http: HttpSecurity) {
        http.authorizeHttpRequests { auth ->
            auth
                .requestMatchers(AdminRoutes.LOGIN, "${AdminRoutes.LOGIN}/**").permitAll()
                .anyRequest().hasRole("ADMIN")
        }
    }

    private fun configureFormLogin(http: HttpSecurity) {
        http
            .formLogin {
                it.loginPage(AdminRoutes.LOGIN)
                    .loginProcessingUrl(AdminRoutes.LOGIN)
                    .defaultSuccessUrl(AdminRoutes.BASE, true)
                    .failureUrl("${AdminRoutes.LOGIN}?error")
                    .permitAll()
            }
            .logout {
                it.logoutUrl(AdminRoutes.LOGOUT)
                    .logoutSuccessUrl("${AdminRoutes.LOGIN}?logout")
                    .permitAll()
            }
    }
}
