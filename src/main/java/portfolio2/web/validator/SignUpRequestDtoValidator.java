package portfolio2.web.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.domain.account.AccountRepository;
import portfolio2.web.dto.SignUpRequestDto;

@Component
@RequiredArgsConstructor
public class SignUpRequestDtoValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        SignUpRequestDto signUpRequestDto = (SignUpRequestDto)o;

        if(accountRepository.existsByEmail(signUpRequestDto.getEmail())){
            errors.rejectValue("userId", "invalid.userId", new Object[]{signUpRequestDto.getEmail()}, "이미 사용중인 아이디 입니다.");
        }
        
        if(accountRepository.existsByEmail(signUpRequestDto.getEmail())){
            errors.rejectValue("email", "invalid.email", new Object[]{signUpRequestDto.getEmail()}, "이미 사용중인 이메일 입니다.");
        }

        if(accountRepository.existsByNickname(signUpRequestDto.getNickname())){
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpRequestDto.getNickname()}, "이미 사용중인 닉네임 입니다.");
        }
    }
}
