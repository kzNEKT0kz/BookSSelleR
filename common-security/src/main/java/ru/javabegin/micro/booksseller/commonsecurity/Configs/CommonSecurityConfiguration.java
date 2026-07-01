package ru.javabegin.micro.booksseller.commonsecurity.Configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javabegin.micro.booksseller.commonsecurity.Security.JwtTokenFilter;
import ru.javabegin.micro.booksseller.commonsecurity.Security.JwtTokenProvider;

@Configuration
public class CommonSecurityConfiguration {

    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        return new JwtTokenProvider(secret, expiration);
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(
            JwtTokenProvider jwtTokenProvider
    ) {
        return new JwtTokenFilter(jwtTokenProvider);
    }
}