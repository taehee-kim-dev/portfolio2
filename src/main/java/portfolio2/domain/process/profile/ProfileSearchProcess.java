package portfolio2.domain.process.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

@RequiredArgsConstructor
@Component
public class ProfileSearchProcess {

    private final AccountRepository accountRepository;

    public Account searchProfile(String userIdToSearch) {
        return accountRepository.findByUserId(userIdToSearch);
    }
}
