package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import portfolio2.service.account.AccountService;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;

import static portfolio2.account.config.TestAccountInfo.*;

@RequiredArgsConstructor
public class SignUpAndLoggedInSecurityContextFactory implements WithSecurityContextFactory<SignUpAndLoggedIn> {

    private final SignUpService signUpService;
    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(SignUpAndLoggedIn signUpAndLoggedIn) {

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID)
                .nickname(TEST_NICKNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        signUpService.signUp(signUpRequestDto);

        // Authentication 만들고 SecurityContext에 넣어주기
        UserDetails customPrincipal = accountService.loadUserByUsername(TEST_USER_ID);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customPrincipal,
                customPrincipal.getPassword(),
                customPrincipal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        return context;
    }
}
