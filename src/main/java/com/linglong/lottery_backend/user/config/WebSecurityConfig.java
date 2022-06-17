package com.linglong.lottery_backend.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Description
 *
 * @author yixun.xing
 * @since 16 三月 2019
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private CustomSavedRequestAwareAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomAuthenticationFailHandler failHandler;

//    @Value("${spring.security.ipAddress}")
//    private String ipAddress;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    //private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors()
                .and()
                .apply(smsCodeAuthenticationSecurityConfig).and()
                .authorizeRequests()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/api/account/getCaptcha",
                        "/api/cashier/sign",
                        "/api/cashier/receive",
                        "/api/activity/participate/list",
                        "/api/withDrawal/receive",
                        "/api/match/**",
                        "/api/activity/details/**",
                        "/api/callback/**",
                        "/api/ticket/refresh",
                        "/api/ticket/refundTicket",
                        "/api/callback/**",
                        "/mobile/**",
                        "/api/apk/**",
                        "/api/job/**",
                        "/api/file/**",
                        "/api/lotteryArea/**",
                        "/api/activity/**",
                        "/api/account/register",
                        "/api/account/modifyPassword",
                        "/api/account/checkUser",
                        "/api/cashier/**",
                        "/api/banner/detail/**","/api/withDrawal/verified","/api/withDrawal/getRecord","/api/prize/review","/api/prize/exceptionOrderRefound").permitAll()
                //                .antMatchers("/api/withDrawal/verified","/api/withDrawal/getRecord").access("hasIpAddress('0:0:0:0:0:0:0:1')")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successHandler)
                // .failureHandler(failureHandler)
                .failureHandler(failHandler)
                .and()
                .httpBasic()
                .and()
                .logout();

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(6);
    }

}
