package portfolio2.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.UrlAndViewNameAboutMain.HOME_URL;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 인증 없이 POST, GET 요청 허용
                .mvcMatchers(HOME_URL, LOGIN_URL, SIGN_UP_URL, FIND_PASSWORD_URL, "/test/**").permitAll()
                // GET 요청만 허용
                .mvcMatchers(HttpMethod.GET, "/account/profile-view/*", CHECK_EMAIL_VERIFICATION_LINK_URL,
                        CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL, "/post-view/*", "/search/**", "/profile").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login");

        http.logout()
                .logoutSuccessUrl("/");

        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    // static 리소스들은 인증하지 말라고(시큐리티 필터들을 적용하지 말아라) 설정.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/library/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
