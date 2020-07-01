package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.CustomPrincipal;

@Component
@RequiredArgsConstructor
public class LogInConfirmProcessForTest {

    public boolean isSomeoneLoggedIn(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null;
    }

    public boolean isLoggedInByUserId(String userId){
        CustomPrincipal customPrincipal
                = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userId.equals(customPrincipal.getSessionAccount().getUserId());
    }
}
