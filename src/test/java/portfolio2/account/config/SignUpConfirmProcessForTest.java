package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;

import static portfolio2.account.config.TestAccountInfo.*;

@Component
@RequiredArgsConstructor
public class SignUpConfirmProcessForTest {

    private final AccountRepository accountRepository;

    public boolean isSignedUpUserId(String userId){
        return accountRepository.existsByUserId(userId);
    }

    public Account getSignedUpAccountInDbByUserId(String userId){
        return accountRepository.findByUserId(userId);
    }
}
