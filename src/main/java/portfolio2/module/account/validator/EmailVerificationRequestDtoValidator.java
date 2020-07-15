package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.EmailVerificationRequestDto;

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

        if(emailVerificationRequestDto.getEmail() == null || emailVerificationRequestDto.getToken() == null){
            errors.rejectValue("email", "invalidLink", "잘못된 링크");
            return;
        }

        // 통과 조건
        // 링크의 이메일이 가입된 회원들의 인증 대기중인 이메일 중에 있어야 하고,
        // 그 이메일 계정에 있는 토큰값과 링크의 토큰이 같아야 한다.
        // 위의 조건이 아니면 모두 유효하지 않은 링크이다.

        Account checkAccount = accountRepository.findByEmailWaitingToBeVerified(emailVerificationRequestDto.getEmail());

        if(checkAccount == null){
            errors.rejectValue("email", "invalidLink", "인증 대기중인 이메일에 존재하지 않음.");
        }else if(checkAccount.getEmailVerificationToken() == null){
            errors.rejectValue("token", "invalidLink", "인증된 이메일에는 존재하나, 해당 계정의 토큰값이 null임.");
        }else if(!checkAccount.getEmailVerificationToken().equals(emailVerificationRequestDto.getToken())){
            errors.rejectValue("token", "invalidLink", "인증 대기중인 이메일에는 존재하나, 토큰값이 일치하지 않음.");
        }

    }
}
