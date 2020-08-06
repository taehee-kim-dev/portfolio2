package portfolio2.module.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.dto.request.SignUpRequestDto;
import portfolio2.module.account.service.process.LogInOrSessionUpdateProcess;
import portfolio2.module.account.service.process.SendingEmailVerificationEmailProcess;
import portfolio2.module.account.service.process.SignUpProcess;

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
