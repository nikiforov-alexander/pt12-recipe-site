package com.techdegree.config;

import com.techdegree.model.User;
import com.techdegree.service.CustomUserDetailsService;
import com.techdegree.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
// what does this prePostEnabled means - no idea ...
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userService;

    // figure out what does this mean
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userService).passwordEncoder(User.PASSWORD_ENCODER);
    }

    // probably this allows our static assets to be
    // used and modified by anyone, allowing us
    // to play with Developer tools in Chrome ...
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**");
    }

    // configures redirect to "/login" page and back
    // for now we took @christherama version of
    // this method.
    // The one from REST authorizeAll mixes up stuff so
    // we'll leave for removed now.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                    .loginPage("/login")
                    .successHandler(loginSuccessHandler())
                    .failureHandler(loginFailureHandler())
                .and()
                .logout()
                    .logoutSuccessUrl("/login")
                .and()
                .csrf();
    }

    // on success we go back home "/"
    // later will change that back to referrer maybe ...
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/");
    }

    // in case of failure we redirect back to "/login" with error
    // flash message
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            request.getSession().setAttribute("flash",
                    new FlashMessage(
                            "Incorrect username and/or password. " +
                            "Please try again.",
                            FlashMessage.Status.FAILURE
                    )
            );
            response.sendRedirect("/login");
        };
    }

    // you ask me what it is : i've no idea ...
    @Bean
    public EvaluationContextExtension securityExtension() {
        return new EvaluationContextExtensionSupport() {
            @Override
            public String getExtensionId() {
                return "sec";
            }

            @Override
            public Object getRootObject() {
                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();
                return new SecurityExpressionRoot(authentication) {
                };
            }
        };
    }
}
