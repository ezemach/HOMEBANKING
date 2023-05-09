package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@EnableWebSecurity
@Configuration
public class  WebAuthorization {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/index.html", "/web/js/**", "/web/css/**", "/web/img/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/login", "/api/logout", "/api/clients").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients/current/cards", "/api/clients/current/accounts", "/api/clients/current/transactions").hasAnyAuthority("CLIENT", "ADMIN")
                .antMatchers("/web/admin/**", "/h2-console", "/rest/**").hasAuthority("ADMIN")
                .antMatchers("/api/clients/current/cards","/api/clients/current/accounts","/api/clients/current" ).hasAuthority("CLIENT")
                .antMatchers("/web/accounts.html", "/web/account.html", "/web/cards.html", "/web/transfers.html","/web/loan-application.html").hasAuthority("CLIENT")
                .antMatchers("/api/clients", "/api/accounts").hasAuthority("CLIENT");

        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

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
