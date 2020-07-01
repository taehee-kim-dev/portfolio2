package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.SignUpProcess;
import portfolio2.dto.account.SignUpRequestDto;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final SignUpProcess signUpProcess;

    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 새로운 계정 생성
        signUpProcess.createNewAccount();
        // 새로운 계정 초기값 설정
        signUpProcess.setInitialInformOfNewAccount(signUpRequestDto);
        // 인증 메일 발송
        signUpProcess.sendEmailVerificationEmail();
        // 로그인
        signUpProcess.login();
    }

}
