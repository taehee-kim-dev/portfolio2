package portfolio2.domain.account;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogInOrSessionUpdateProcess {

    public void loginOrSessionUpdate(Account accountOrSessionObject) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal(accountOrSessionObject),
                accountOrSessionObject.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
