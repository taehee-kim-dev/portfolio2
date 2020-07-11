package portfolio2.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class SendingEmailVerificationEmailProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcess emailSendingProcess;

    public boolean canSendEmailVerificationEmail(Account account) {
        Account accountToCheck = accountRepository.findByUserId(account.getUserId());
        return accountToCheck.canSendEmailVerificationEmail();
    }

    public Account sendEmailVerificationEmail(Account account){
        // 이미 전송 허가 받은 상태
        Account accountToSendEmail = accountRepository.findByUserId(account.getUserId());
        // 인증 상태 false로 변경
        accountToSendEmail.setEmailVerified(false);
        // 인증된 메일 null로 변경
        accountToSendEmail.setVerifiedEmail(null);
        // 토큰 새로 생성
        accountToSendEmail.generateEmailCheckToken();
        // 인증 메일 전송
        emailSendingProcess.sendEmailVerificationEmail(accountToSendEmail);
        // 카운트 횟수 초기화 or 증가
        accountToSendEmail.increaseOrResetCountOfSendingEmailVerificationEmail();
        return accountToSendEmail;
    }
}
