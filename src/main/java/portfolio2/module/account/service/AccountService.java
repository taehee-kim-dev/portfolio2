package portfolio2.module.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.CustomPrincipal;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userIdOrEmail) throws UsernameNotFoundException {

        Account accountInDb = accountRepository.findByUserId(userIdOrEmail);

        if (accountInDb == null) {
            accountInDb = accountRepository.findByVerifiedEmail(userIdOrEmail);
        }

        if (accountInDb == null) {
            throw new UsernameNotFoundException(userIdOrEmail);
        }

        return new CustomPrincipal(accountInDb);
    }
}
