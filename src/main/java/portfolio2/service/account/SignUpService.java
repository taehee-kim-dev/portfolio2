package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.domain.account.SignUpProcess;
import portfolio2.domain.email.SendingEmailVerificationEmailProcess;
import portfolio2.dto.account.SignUpRequestDto;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final SignUpProcess signUpProcess;
    private final SendingEmailVerificationEmailProcess sendingEmailVerificationEmailProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 회원가입 정보를 토대로,
        // 새로운 계정 생성 및 저장.
        Account signedUpAccount = signUpProcess.createNewAccountAndSaveWith(signUpRequestDto);
        // 이메일 인증 메일 발송
        Account emailSentAccount
                = sendingEmailVerificationEmailProcess.sendEmailVerificationEmail(signedUpAccount);
        // 로그인
        logInOrSessionUpdateProcess.loginOrSessionUpdate(emailSentAccount);
    }

}
