package portfolio2.module.account.service.process;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.CustomPrincipal;

import java.util.List;

@Component
public class LogInOrSessionUpdateProcess {

    public Account loginOrSessionUpdate(Account account) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);

        return account;
    }
}
