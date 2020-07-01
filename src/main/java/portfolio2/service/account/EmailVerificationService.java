package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.EmailVerificationProcess;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.dto.account.profileupdate.AccountEmailUpdateRequestDto;

import javax.validation.Valid;

@Transactional
@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final EmailVerificationProcess emailVerificationProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public boolean checkEmailVerificationLink(String email, String token) {
        // 맞는 링크인지 확인
        // 아니면 바로 false return
        return emailVerificationProcess.isValidLink(email, token);
    }

    public Account emailVerifyAndLogIn() {
        // 이메일 인증
        Account emailVerifiedAccountInDb = emailVerificationProcess.verifyEmail();
        // EmailVerificationProcess 필드 초기화
        emailVerificationProcess.clearField();
        // 로그인 유무와 관계없이 무조건 현재 인증 링크에 해당하는 계정으로 로그인 후 해당 세션계정 객체 반환
        return logInOrSessionUpdateProcess.loginOrSessionUpdate(emailVerifiedAccountInDb);
    }



    public boolean canSendEmailVerificationEmail(Account sessionAccount){
        return emailVerificationProcess.canSendEmailVerificationEmail(sessionAccount);
    }

    public void sendEmailVerificationEmail(AccountEmailUpdateRequestDto accountEmailUpdateRequestDto) {
        emailVerificationProcess.changeAccountState(accountEmailUpdateRequestDto);
        emailVerificationProcess.sendEmailVerificationEmail();
        emailVerificationProcess.clearField();
    }
}
