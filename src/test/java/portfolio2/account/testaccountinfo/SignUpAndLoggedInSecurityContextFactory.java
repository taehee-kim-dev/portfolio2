package portfolio2.account.testaccountinfo;

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

@RequiredArgsConstructor
public class SignUpAndLoggedInSecurityContextFactory implements WithSecurityContextFactory<SignUpAndLoggedIn> {

    private final SignUpService signUpService;
    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(SignUpAndLoggedIn signUpAndLoggedIn) {

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TestAccountInfo.TEST_USER_ID)
                .nickname(TestAccountInfo.TEST_NICKNAME)
                .email(TestAccountInfo.TEST_EMAIL)
                .password(TestAccountInfo.TEST_PASSWORD)
                .build();

        signUpService.signUp(signUpRequestDto);

        // Authentication 만들고 SecurityContext에 넣어주기
        UserDetails customPrincipal = accountService.loadUserByUsername(TestAccountInfo.TEST_USER_ID);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, customPrincipal.getPassword(), customPrincipal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
