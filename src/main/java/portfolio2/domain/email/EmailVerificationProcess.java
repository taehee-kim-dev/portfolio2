package portfolio2.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.email.EmailSendingProcess;
import portfolio2.dto.account.EmailVerificationRequestDto;

@RequiredArgsConstructor
@Component
public class EmailVerificationProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcess emailSendingProcess;

    // 이메일 인증 처리
    public Account verifyEmail(EmailVerificationRequestDto emailVerificationRequestDto) {
        // 인증 대기중인 이메일로 해당 계정 찾음.
        Account accountToBeVerified
                = accountRepository.findByEmailWaitingToBeVerified(emailVerificationRequestDto.getEmail());
        
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
        // 인증한 계정 저장 후 반환
        return accountToBeVerified;
    }
}
