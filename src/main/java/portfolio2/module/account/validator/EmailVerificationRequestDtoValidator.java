package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.EmailVerificationRequestDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailVerificationRequestDtoValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EmailVerificationRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        EmailVerificationRequestDto emailVerificationRequestDto = (EmailVerificationRequestDto)o;

        // 통과 조건
        // 링크의 이메일이 가입된 회원들의 인증 대기중인 이메일 중에 있어야 하고,
        // 그 이메일 계정에 있는 토큰값과 링크의 토큰이 같아야 한다.
        // 위의 조건이 아니면 모두 유효하지 않은 링크이다.

        Account checkAccount = accountRepository.findByUserIdAndEmailWaitingToBeVerifiedAndEmailVerificationToken(
                emailVerificationRequestDto.getUserId(), emailVerificationRequestDto.getEmail(), emailVerificationRequestDto.getToken());

        if(checkAccount == null){
            errors.rejectValue("email", "invalidLink", "해당 계정 없음.");
        }
    }
}
