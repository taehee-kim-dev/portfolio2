package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;

@RequiredArgsConstructor
@Component
public class ProfileSearchProcess {

    private final AccountRepository accountRepository;

    public Account searchProfile(String userIdToSearch) {
        return accountRepository.findByUserId(userIdToSearch);
    }
}
