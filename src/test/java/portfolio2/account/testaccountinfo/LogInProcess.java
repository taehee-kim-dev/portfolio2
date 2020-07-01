package portfolio2.account.testaccountinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import portfolio2.service.account.AccountService;

@RequiredArgsConstructor
@Component
public class LogInProcess {

    private final AccountService accountService;

    public void logIn(String userId){
        // Authentication 만들고 SecurityContext에 넣어주기
        UserDetails customPrincipal = accountService.loadUserByUsername(userId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customPrincipal, customPrincipal.getPassword(), customPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
