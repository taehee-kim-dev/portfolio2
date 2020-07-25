package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.SignUpRequestDto;

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

        String userIdPattern = "^[a-z0-9-_.]{3,20}$";
        String nicknamePattern = "^[a-zA-Z가-힣0-9-_.]{2,20}$";
        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        String passwordPattern = "^[\\S0-9]{8,50}$";


        String userId = signUpRequestDto.getUserId();

        if(!(userId.matches(userIdPattern))){
            errors.rejectValue("userId", "invalidFormatUserId",
                    "3~20자의 영문 소문자, 숫자와 특수문자 (-), (_), (.)만 사용 가능합니다.");
        }else if(accountRepository.existsByUserId(userId)){
            errors.rejectValue("userId", "userIdAlreadyExists",
                    "이미 사용중인 아이디 입니다.");
        }


        String nickname = signUpRequestDto.getNickname();

         if(!(nickname.matches(nicknamePattern))){
            errors.rejectValue("nickname", "invalidFormatNickname",
                    "2~20자의 영문, 한글(단순 모음, 자음 제외), 숫자와 특수문자 (-), (_), (.)만 사용 가능합니다.");
        }else if(accountRepository.existsByNickname(nickname)){
            errors.rejectValue("nickname", "nicknameAlreadyExists",
                    "이미 사용중인 닉네임 입니다.");
        }


        String email = signUpRequestDto.getEmail();

        if(!(email.matches(emailPattern))){
            errors.rejectValue("email", "invalidFormatEmail",
                    "이메일 형식에 맞지 않습니다.");
        }else if(accountRepository.existsByVerifiedEmail(email)){
            errors.rejectValue("email", "emailAlreadyExists",
                    "이미 사용중인 이메일 입니다.");
        }

        String password = signUpRequestDto.getPassword();

        if(!(password.matches(passwordPattern))){
            errors.rejectValue("password", "invalidFormatPassword",
                    "8~50자의 공백을 제외한 문자, 숫자만 사용 가능합니다.");
        }

    }
}
