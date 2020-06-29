package portfolio2.domain.account;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomPrincipal extends User {

    private Account sessionAccount;


    public CustomPrincipal(Account accountInDb){
        super(accountInDb.getUserId(), accountInDb.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.sessionAccount = accountInDb;
    }
}
