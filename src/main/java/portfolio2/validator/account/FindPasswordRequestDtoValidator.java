package portfolio2.validator.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.FindPasswordRequestDto;

@RequiredArgsConstructor
@Component
public class FindPasswordRequestDtoValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(FindPasswordRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        FindPasswordRequestDto findPasswordRequestDto = (FindPasswordRequestDto)o;
        
        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        String email = findPasswordRequestDto.getEmail();

        if(!(email.matches(emailPattern))){
            errors.rejectValue("email", "invalidFormatEmail","이메일 형식에 맞지 않습니다.");
        }else if(!accountRepository.existsByVerifiedEmail(email)){
            errors.rejectValue("email", "emailNotExists", "가입되어 있지 않거나, 인증되어 있지 않은 이메일 입니다.");
        }

    }
}
