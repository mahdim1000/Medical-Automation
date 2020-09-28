//package com.example.epointment.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
//import org.springframework.security.web.savedrequest.NullRequestCache;
//import org.springframework.security.core.userdetails.User;
//
//import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.Base64;
//import java.util.Random;
//
////@Configuration
////@EnableWebSecurity
//public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
//
//
//    private static final String[] PUBLIC_MATCHERS = {
//            "/api",
//            "/api/*",
//            "/api/**",
//            "/**",
//            "/*/**",
//            "/api/doctor",
//            "/api/doctor/**"
//    };
//
////    @Override
////    protected  void configure(AuthenticationManagerBuilder auth) throws Exception{
////        auth.inMemoryAuthentication()
////                .withUser("doctor").password("doctor").roles("DOCTOR")
////                .and()
////                .withUser("clinic").password("clinic").roles("CLINIC")
////                .and()
////                .withUser("patient").password("patient").roles("PATIENT");
////    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http
//                .httpBasic().and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                .antMatchers("/api", "/api/**").permitAll()
//                .anyRequest().authenticated()
////                .and().formLogin().disable().httpBasic().disable()
//                .and().csrf().disable()
//                .sessionManagement().maximumSessions(1).and()
//                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//                .enableSessionUrlRewriting(true)
//                .and().requestCache().requestCache(new NullRequestCache());
//
//    }
//
//
//        public static String addSalt(String pass){
//            return "2348jfj#$9df"+pass+"3%q@w!4#R$%/^dTR&*(oi()o.";
//        }
//
//
//
//
//
//
//
//}
