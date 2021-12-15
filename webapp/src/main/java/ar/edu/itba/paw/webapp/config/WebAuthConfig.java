package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtForbiddenEntryPoint jwtForbiddenEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
       /* http.csrf().disable()
                *//* Permit just /users/authenticate or /register *//*
                .authorizeRequests()
                .anyRequest().permitAll().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
*/
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtForbiddenEntryPoint)
            .and().authorizeRequests()

                // User Profile Controller
                .antMatchers(HttpMethod.POST, "/api/users/{id:[\\d]+}/password", "/api/users/{id:[\\d]+}/password/")
                .authenticated()

                .antMatchers(HttpMethod.POST, "/api/users/password", "/api/users/password/")
                .anonymous()

                .antMatchers(HttpMethod.PUT,"/api/users/{id:[\\d]+}", "/api/users/{id:[\\d]+}/","/api/users/{id:[\\d]+}/enable_modding/{value:[\\s]+}", "/api/users/{id:[\\d]+}/enable_modding/{value:[\\s]+}/")
                .authenticated()

                // Register Controller

                .antMatchers(HttpMethod.POST, "/api/register", "/api/register/","/api/register/forgot_password", "/api/register/forgot_password/")
                .anonymous()

                .antMatchers(HttpMethod.POST, "/api/register/resend_token", "/api/register/resend_token/")
                .authenticated()

                // Posts Controller
                .antMatchers(HttpMethod.POST, "/api/posts", "/api/posts/","/api/posts/{id:[\\d]+}/up_vote", "/api/posts/{id:[\\d]+}/up_vote/","/api/posts/{id:[\\d]+}/down_vote", "/api/posts/{id:[\\d]+}/down_vote/","/api/posts/{id:[\\d]+}/answers/{commentId:[\\d]+}/up_vote", "/api/posts/{id:[\\d]+}/answers/{commentId:[\\d]+}/up_vote/","/api/posts/{id:[\\d]+}/answers/{commentId:[\\d]+}/down_vote", "/api/posts/{id:[\\d]+}/answers/{commentId:[\\d]+}/down_vote/","/api/posts/{id:[\\d]+}/answers", "/api/posts/{id:[\\d]+}/answers/")
                .authenticated()

                .antMatchers(HttpMethod.PUT,"/api/posts/{id:[\\d]+}", "/api/posts/{id:[\\d]+}/")
                .authenticated()

                .antMatchers(HttpMethod.DELETE,"/api/posts/{id:[\\d]+}", "/api/posts/{id:[\\d]+}/","/api/posts/{id:[\\d]+}/answers/{commentId:[\\d]+}","/api/posts/{id:[\\\\d]+}/answers/{commentId:[\\\\d]+}/")
                .authenticated()

                // Mod Controller
                .antMatchers("/api/mod/**")
                .authenticated()

                // Techs Controller
                .antMatchers(HttpMethod.POST,"/api/techs","/api/techs/")
                .hasAnyRole("ADMIN","MODERATOR")

                .antMatchers(HttpMethod.POST,"/api/techs/**")
                .hasRole("USER_ENABLED")

                .antMatchers(HttpMethod.PUT,"/api/techs/{id:[\\d]+}","/api/techs/{id:[\\d]+}/")
                .authenticated()

                .antMatchers(HttpMethod.DELETE,"/api/techs/**")
                .authenticated()


                // Every request
                .antMatchers("/api/**").permitAll()

            .and().csrf().disable()
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JWTAuthenticationFilter(authenticationManager(),jwtTokenUtil),UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/403");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
