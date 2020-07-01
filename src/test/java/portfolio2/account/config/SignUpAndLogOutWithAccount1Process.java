package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;

@Component
@RequiredArgsConstructor
public class SignUpAndLogOutWithAccount1Process {

    private final SignUpService signUpService;

    public void signUpAndLogOut(){
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TestAccountInfo.TEST_USER_ID_1)
                .nickname(TestAccountInfo.TEST_NICKNAME_1)
                .email(TestAccountInfo.TEST_EMAIL_1)
                .password(TestAccountInfo.TEST_PASSWORD_1)
                .build();

        signUpService.signUp(signUpRequestDto);

        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
