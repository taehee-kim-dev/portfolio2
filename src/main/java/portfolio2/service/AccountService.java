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
import portfolio2.web.dto.PasswordUpdateRequestDto;
import portfolio2.web.dto.ProfileUpdateRequestDto;
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


    public void processNewAccount(SignUpRequestDto signUpRequestDto) {
        Account newAccount = saveNewAccount(signUpRequestDto);
        sendSignUpConfirmEmail(newAccount);
        this.loginOrUpdateSessionAccount(newAccount);
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

        Account accountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        accountInDb.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(accountInDb.getEmail());
        mailMessage.setSubject("ShareMind 회원가입 이메일 인증");
        mailMessage.setText("/check-email-token?token=" + accountInDb.getEmailCheckToken() +
                "&email=" + accountInDb.getEmail());
        javaMailSender.send(mailMessage);
    }


    public void loginOrUpdateSessionAccount(Account account) {

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
        Account accountInDb = accountRepository.findByUserId(account.getUserId());
        accountInDb.completeSignUp();
        loginOrUpdateSessionAccount(accountInDb);
    }

    public void updateProfile(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        sessionAccount.setBio(profileUpdateRequestDto.getBio());
        sessionAccount.setOccupation(profileUpdateRequestDto.getOccupation());
        sessionAccount.setLocation(profileUpdateRequestDto.getLocation());
        sessionAccount.setProfileImage(profileUpdateRequestDto.getProfileImage());
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
    }

    public void updatePassword(Account sessionAccount, PasswordUpdateRequestDto passwordUpdateRequestDto) {
        sessionAccount.setPassword(passwordEncoder.encode(passwordUpdateRequestDto.getNewPassword()));
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
        this.sendPasswordChangeNotificationEmail(sessionAccount);
    }

    public void sendPasswordChangeNotificationEmail(Account sessionAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(sessionAccount.getEmail());
        mailMessage.setSubject("ShareMind 비밀번호 변경 알림");
        mailMessage.setText(sessionAccount.getUserId() + "의 비밀번호가 변경되었습니다.");
        javaMailSender.send(mailMessage);
    }
}
