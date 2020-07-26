package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.EmailVerificationRequestDto;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EmailVerificationProcess {

    private final AccountRepository accountRepository;

    // 이메일 인증 처리
    public Account verifyEmail(EmailVerificationRequestDto emailVerificationRequestDto) {

        List<Account> otherAccount = accountRepository.findAllByEmailWaitingToBeVerifiedAndUserIdNot(
                emailVerificationRequestDto.getEmail(), emailVerificationRequestDto.getUserId()
        );

        otherAccount.forEach(account -> {
            account.setEmailWaitingToBeVerified(null);
            account.setEmailVerificationToken(null);
        });

        Account accountToBeVerified
                = accountRepository.findByUserId(emailVerificationRequestDto.getUserId());
        
        // 인증 대기중인 이메일 주소를 인증된 이메일 주소로 설정
         accountToBeVerified.setVerifiedEmail(accountToBeVerified.getEmailWaitingToBeVerified());
        // 이메일 인증되었음으로 설정.
        accountToBeVerified.setEmailVerified(true);
        // 최초로 인증된 이메일 이력이 없다면, 해당 이력 있음으로 설정.
        if(!accountToBeVerified.isEmailFirstVerified())
            accountToBeVerified.setEmailFirstVerified(true);
        // 토큰값 null로 설정
        accountToBeVerified.setEmailVerificationToken(null);
        // 인증 대기중인 이메일 값 null로 설정
        accountToBeVerified.setEmailWaitingToBeVerified(null);
        // 이메일 알림 설정 모두 true로 변경
        accountToBeVerified.setNotificationLikeOnMyPostByEmail(true);
        accountToBeVerified.setNotificationLikeOnMyCommentByEmail(true);
        accountToBeVerified.setNotificationCommentOnMyPostByEmail(true);
        accountToBeVerified.setNotificationCommentOnMyCommentByEmail(true);
        accountToBeVerified.setNotificationNewPostWithMyInterestTagByEmail(true);
        accountToBeVerified.setNotificationMyInterestTagAddedToExistingPostByEmail(true);
        // 인증한 계정 반환
        return accountToBeVerified;
    }

    public boolean isEmailVerifiedAccountLoggedIn(Account sessionAccount, Account emailVerifiedAccountInDb) {
        // 현재 로그인중인 세션 사용자와 이메일 인증이 된 사용자가 같은가?
        return sessionAccount.getUserId().equals(emailVerifiedAccountInDb.getUserId());
    }
}
