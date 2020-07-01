package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.domain.account.SignUpProcess;
import portfolio2.dto.account.SignUpRequestDto;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final SignUpProcess signUpProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 새로운 계정 생성
        signUpProcess.createNewAccount();
        // 새로운 계정 초기값 설정
        signUpProcess.setInitialInformOfNewAccount(signUpRequestDto);
        // 인증 메일 발송
        Account newAccount = signUpProcess.sendEmailVerificationEmail();
        signUpProcess.clearNewAccountField();
        // 로그인
        logInOrSessionUpdateProcess.loginOrSessionUpdate(newAccount);
    }

}
