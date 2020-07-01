package portfolio2.account.testaccountinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;

@Component
@RequiredArgsConstructor
public class SignUpAndLogInWithAccount2Process {

    private final SignUpService signUpService;

    public void signUpAndLogIn(){
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TestAccountInfo.TEST_USER_ID_2)
                .nickname(TestAccountInfo.TEST_NICKNAME_2)
                .email(TestAccountInfo.TEST_EMAIL_2)
                .password(TestAccountInfo.TEST_PASSWORD_2)
                .build();

        signUpService.signUp(signUpRequestDto);
    }
}
