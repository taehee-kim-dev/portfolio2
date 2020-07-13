package portfolio2.domain.process.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.SignUpRequestDto;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Component
public class SignUpProcess {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account createNewAccountAndSaveWith(SignUpRequestDto signUpRequestDto) {
        signUpRequestDto.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        Account newAccount = new Account();
        newAccount.setUserId(signUpRequestDto.getUserId());
        newAccount.setNickname(signUpRequestDto.getNickname());
        newAccount.setPassword(signUpRequestDto.getPassword());
        newAccount.setEmailWaitingToBeVerified(signUpRequestDto.getEmail());
        newAccount.setJoinedAt(LocalDateTime.now());
        return accountRepository.save(newAccount);
    }
}
