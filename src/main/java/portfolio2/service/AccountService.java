package portfolio2.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.UserAccount;
import portfolio2.web.dto.SignUpRequestDto;

import javax.validation.Valid;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;


    public Account processNewAccount(SignUpRequestDto signUpRequestDto) {
        Account newAccount = saveNewAccount(signUpRequestDto);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpRequestDto signUpRequestDto) {
        Account account = Account.builder()
                .userId(signUpRequestDto.getUserId())
                .email(signUpRequestDto.getEmail())
                .nickname(signUpRequestDto.getNickname())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .sendCheckEmailCount(0)
                .notificationByEmail(true)
                .notificationByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("ShareMind 회원가입 이메일 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void resendSignUpConfirmEmail(Account sessionAccount) {

        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        existingAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(existingAccount.getEmail());
        mailMessage.setSubject("ShareMind 회원가입 이메일 인증");
        mailMessage.setText("/check-email-token?token=" + existingAccount.getEmailCheckToken() +
                "&email=" + existingAccount.getEmail());
        javaMailSender.send(mailMessage);
    }


    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Account account = accountRepository.findByUserId(userId);

        if (account == null) {
            throw new UsernameNotFoundException(userId);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        Account existingAccount = accountRepository.findByUserId(account.getUserId());
        existingAccount.completeSignUp();
        login(existingAccount);
    }
}
