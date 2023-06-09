package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@CrossOrigin
@EnableWebSecurity
@Configuration
public class WebAuthorization {
    @Bean
    protected SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
        //Cross-Origin Resource Sharing
        http.cors().and().authorizeRequests()
                .antMatchers("/web/index.html", "/web/js/**", "/web/css/**", "/web/img/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/login", "/api/logout").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients/current/cards", "/api/clients/current/accounts", "/api/clients/current/transactions","api/loans/create","api/loans/payments").hasAnyAuthority("CLIENT", "ADMIN")
                .antMatchers("/web/admin/**", "/h2-console", "/rest/**","web/manager.html").hasAuthority("ADMIN")
                .antMatchers("/api/clients/current/cards","/api/clients/current/accounts","/api/clients/current","/api/loans" ).hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/accounts.html", "/web/account.html", "/web/cards.html", "/web/transfers.html","/web/loan-application.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/api/clients", "/api/accounts", "/api/cards").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/clients/current/cards").hasAnyAuthority("CLIENT","ADMIN");

//        .anyRequest().denyAll();

        //config de inicio sesion
        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        //config cierre sesion
        http.logout().logoutUrl("/api/logout").deleteCookies("JSESSIONID");

        //turn off checking for CSRF tokens
        http.csrf().disable();

        // disabling fameOptions so h2-console can be accessed
        http.headers().frameOptions().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        return http.build();

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }


}
