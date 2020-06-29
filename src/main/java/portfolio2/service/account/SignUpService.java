package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.SignUp;
import portfolio2.dto.account.SignUpRequestDto;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SignUpService {

    private final SignUp signUp;

    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 새로운 계정 생성
        signUp.createNewAccount();
        // 새로운 계정 초기값 설정
        signUp.setInitialInformOfNewAccount(signUpRequestDto);
        // 인증 메일 발송
        signUp.sendEmailVerificationEmail();
        // 로그인
        signUp.login();
    }

}
