package portfolio2.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import portfolio2.domain.tag.Tag;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.UserAccount;
import portfolio2.dto.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


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
                .sendLoginEmailCount(0)
                .notificationLikeOnMyPostByWeb(true)
                .notificationLikeOnMyReplyByWeb(true)
                .notificationReplyOnMyPostByWeb(true)
                .notificationReplyOnMyReplyByWeb(true)
                .notificationNewPostWithMyTagByWeb(true)
                .notificationLikeOnMyPostByEmail(false)
                .notificationLikeOnMyReplyByEmail(false)
                .notificationReplyOnMyPostByEmail(false)
                .notificationReplyOnMyReplyByEmail(false)
                .notificationNewPostWithMyTagByEmail(false)
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
    public UserDetails loadUserByUsername(String userIdOrEmail) throws UsernameNotFoundException {

        Account account = accountRepository.findByUserId(userIdOrEmail);

        if (account == null) {
            account = accountRepository.findByEmail(userIdOrEmail);
        }

        if (account == null) {
            throw new UsernameNotFoundException(userIdOrEmail);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        Account accountInDb = accountRepository.findByUserId(account.getUserId());
        accountInDb.completeSignUp();
        loginOrUpdateSessionAccount(accountInDb);
    }

    public void updateProfile(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        modelMapper.map(profileUpdateRequestDto, sessionAccount);
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

    public void updateNotification(Account sessionAccount, NotificationUpdateRequestDto notificationUpdateRequestDto) {
        modelMapper.map(notificationUpdateRequestDto, sessionAccount);
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
    }

    public void updateAccount(Account sessionAccount, AccountUpdateRequestDto accountUpdateRequestDto) {
        modelMapper.map(accountUpdateRequestDto, sessionAccount);
        accountRepository.save(sessionAccount);
        loginOrUpdateSessionAccount(sessionAccount);
    }

    public void sendLoginEmail(Account account) {
        Account accountInDb = accountRepository.findByUserId(account.getUserId());
        accountInDb.generateLoginEmailToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(accountInDb.getEmail());
        mailMessage.setSubject("ShareMind 이메일로 로그인하기 링크");
        mailMessage.setText("/login-by-email?token=" + accountInDb.getEmailLoginToken() +
                "&email=" + accountInDb.getEmail());
        javaMailSender.send(mailMessage);
    }

    public List<String> getTag(Account sessionAccount) {
        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        Set<Tag> tagOfExistingAccount = existingAccount.getTag();
        List<String> tagListOfExistingAccount = tagOfExistingAccount.stream().map(Tag::getTitle).collect(Collectors.toList());
        return tagListOfExistingAccount;
    }

    public void addTag(Account sessionAccount, Tag newTag) {
        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        if(existingAccount != null){
            existingAccount.getTag().add(newTag);
        }
    }



    public void removeTag(Account sessionAccount, Tag tagToRemove) {
        Account existingAccount = accountRepository.findByUserId(sessionAccount.getUserId());
        if(existingAccount != null){
            existingAccount.getTag().remove(tagToRemove);
        }
    }
}
