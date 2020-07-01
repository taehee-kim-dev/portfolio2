package portfolio2.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.SendEmail;
import portfolio2.dto.account.SignUpRequestDto;

import java.time.LocalDateTime;
import java.util.List;

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

    public void sendEmailVerificationEmail() {
        this.newAccount.generateEmailCheckToken();
        sendEmail.sendEmailVerificationEmail(this.newAccount);
    }

    public void login() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal(this.newAccount),
                this.newAccount.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
        this.newAccount = null;
    }
}
