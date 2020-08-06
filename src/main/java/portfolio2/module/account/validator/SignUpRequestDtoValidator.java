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


    /**
     * regex에서 escape는 backslash('\')를 맨 앞에 붙여서 한다.
     * 그런데, Java의 String에서 예를들어 \t를 "\t"로 쓰면 그냥 tab이 되어버린다.
     * 따라서 Java의 String으로 "\t"라는 문자열을 regex expression으로 표현하려면
     * "\t" 문자열 자체를 표현해야 하는데, 그러려면 "\\t"로 써야한다.
     * 즉,
     * Java String = "\\t" -> "\t"로 표현됨 -> regex expression에 \t를 전달할 수 있음.
     * "\\"를 표현하고 싶으면 각 backslash를 escape 해 줘야 한다.
     * 그래야 각 backslash가 문자열로 표현되기 때문이다.
     * 즉,
     * Java String = "\\\\" -> "\\"로 표현됨 -> regex expression에 \\를 전달할 수 있음.
     * */

    @Override
    public void validate(Object o, Errors errors) {

        SignUpRequestDto signUpRequestDto = (SignUpRequestDto)o;

        String userIdPattern = "^[a-z0-9\\-_.]{3,20}$";
        String nicknamePattern = "^[a-zA-Z가-힣0-9\\-_.]{2,20}$";
        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^\\-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        String passwordPattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9`~!@#$%^&*(\\\\)_+\\-=\\[\\];',./{}|:\"<>?]{8,50}$";


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
