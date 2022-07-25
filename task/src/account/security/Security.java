package account.security;

import account.exception.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()

                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handle auth error
                .and()

                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()

                .authorizeRequests()
                .antMatchers("/api/security/events/**")
                .hasAnyAuthority("ROLE_AUDITOR")
                .antMatchers("/api/auth/changepass/**")
                .hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT", "ROLE_ADMINISTRATOR")
                .antMatchers("/api/empl/payment/**")
                .hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT")
                .antMatchers("/api/admin/user/**")
                .hasAnyAuthority("ROLE_ADMINISTRATOR")
                .antMatchers("/api/acct/payments/**")
                .hasAnyAuthority("ROLE_ACCOUNTANT")

                .antMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}