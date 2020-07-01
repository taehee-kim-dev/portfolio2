package portfolio2.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.SendEmail;
import portfolio2.dto.account.SignUpRequestDto;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class SignUpProcess {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendEmail sendEmail;

    private Account newAccount;

    public void createNewAccount() {
        this.newAccount = new Account();
    }

    public void setInitialInformOfNewAccount(SignUpRequestDto signUpRequestDto) {
        signUpRequestDto.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        this.newAccount.setUserId(signUpRequestDto.getUserId());
        this.newAccount.setNickname(signUpRequestDto.getNickname());
        this.newAccount.setPassword(signUpRequestDto.getPassword());
        this.newAccount.setEmailWaitingToBeVerified(signUpRequestDto.getEmail());
        this.newAccount.setJoinedAt(LocalDateTime.now());
        accountRepository.save(this.newAccount);
    }

    public Account sendEmailVerificationEmail() {
        this.newAccount.generateEmailCheckToken();
        sendEmail.sendEmailVerificationEmail(this.newAccount);
        return this.newAccount;
    }

    public void clearNewAccountField(){
        this.newAccount = null;
    }
}
