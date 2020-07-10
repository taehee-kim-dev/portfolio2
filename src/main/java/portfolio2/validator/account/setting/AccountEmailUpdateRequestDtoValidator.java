package portfolio2.validator.account.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.setting.AccountEmailUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class AccountEmailUpdateRequestDtoValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountEmailUpdateRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = (AccountEmailUpdateRequestDto)o;

        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if(!(accountEmailUpdateRequestDto.getEmail().matches(emailPattern))){
            errors.rejectValue("email", "invalidFormatEmail", "이메일 형식에 맞지 않습니다.");
        }else if(accountRepository.existsByVerifiedEmail(accountEmailUpdateRequestDto.getEmail())){
            errors.rejectValue("email", "emailAlreadyExists", "이미 사용중인 이메일 입니다.");
        }

    }
}
