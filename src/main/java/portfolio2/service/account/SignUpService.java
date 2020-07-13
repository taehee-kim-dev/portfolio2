package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.process.account.LogInOrSessionUpdateProcess;
import portfolio2.domain.process.account.SignUpProcess;
import portfolio2.domain.process.email.SendingEmailVerificationEmailProcess;
import portfolio2.dto.request.account.SignUpRequestDto;

@Transactional
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final SignUpProcess signUpProcess;
    private final SendingEmailVerificationEmailProcess sendingEmailVerificationEmailProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public Account signUp(SignUpRequestDto signUpRequestDto) {
        // 회원가입 정보를 토대로,
        // 새로운 계정 생성 및 저장.
        Account signedUpAccount = signUpProcess.createNewAccountAndSaveWith(signUpRequestDto);
        // 이메일 인증 메일 발송
        Account emailSentAccount
                = sendingEmailVerificationEmailProcess.sendEmailVerificationEmail(signedUpAccount);
        // 로그인
        return logInOrSessionUpdateProcess.loginOrSessionUpdate(emailSentAccount);
    }

}
