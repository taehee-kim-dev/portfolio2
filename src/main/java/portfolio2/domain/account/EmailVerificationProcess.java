package portfolio2.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.EmailSendingProcess;
import portfolio2.dto.account.profileupdate.AccountEmailUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class EmailVerificationProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcess emailSendingProcess;

    private Account accountToBeVerified;

    public boolean isValidLink(String email, String token) {
        // 해당 이메일이 인증 대기중인 이메일이 맞는지 검사
        // 없으면 return false
        Account accountInDb = accountRepository.findByEmailWaitingToBeVerified(email);
        if(accountInDb == null)
            return false;
        // 있으면 계정의 토큰과 링크의 토큰이 일치하는지 검사.
        // 일치하지 않으면 return false
        if(!accountInDb.getEmailVerificationToken().equals(token))
            return false;
        // 일치하면 유요한 링크
        // 일치하면 클래스의 필드에 해당 계정 저장하고 return true
        this.accountToBeVerified = accountInDb;
        return true;
    }

    // 이메일 인증 처리
    public Account verifyEmail() {
        // 인증 대기중인 이메일 주소를 인증된 이메일 주소로 설정
        this.accountToBeVerified.setVerifiedEmail(this.accountToBeVerified.getEmailWaitingToBeVerified());
        // 이메일 인증되었음으로 설정.
        this.accountToBeVerified.setEmailVerified(true);
        // 최초로 인증된 이메일 이력이 없다면, 해당 이력 있음으로 설정.
        if(!this.accountToBeVerified.isEmailFirstVerified())
            this.accountToBeVerified.setEmailFirstVerified(true);
        // 토큰값 null로 설정
        this.accountToBeVerified.setEmailVerificationToken(null);
        // 인증 대기중인 이메일 값 null로 설정
        this.accountToBeVerified.setEmailWaitingToBeVerified(null);
        // 인증한 계정 저장 후 반환
        return accountToBeVerified;
    }

    public void clearField() {
        this.accountToBeVerified = null;
    }
}
