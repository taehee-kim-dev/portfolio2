package portfolio2.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.SignUpRequestDto;

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

        String userIdPattern = "^[a-zA-Z0-9]+$";
        String nicknamePattern = "^[a-zA-Z가-힣0-9]+$";

        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if(signUpRequestDto.getUserId().length() < 5){
            errors.rejectValue("userId", "tooShortUserId","아이디는 5자 이상이어야 합니다.");
        }else if(signUpRequestDto.getUserId().length() > 20){
            errors.rejectValue("userId", "tooLongUserId","아이디는 20자 이내여야 합니다.");
        }else if(!(signUpRequestDto.getUserId().matches(userIdPattern))){
            errors.rejectValue("userId", "invalidFormatUserId", "형식에 맞지 않는 아이디 입니다.");
        }

        if(signUpRequestDto.getNickname().length() < 3){
            errors.rejectValue("nickname", "invalidNickname", "닉네임은 3자 이상이어야 합니다.");
        }else if(signUpRequestDto.getNickname().length() >15){
            errors.rejectValue("nickname", "invalidNickname", "닉네임은 15자 이내여야 합니다.");
        }else if(!(signUpRequestDto.getNickname().matches(nicknamePattern))){
            errors.rejectValue("nickname", "invalidNickname", "형식에 맞지 않는 닉네임 입니다.");
        }

        if(!(signUpRequestDto.getEmail().matches(emailPattern))){
            errors.rejectValue("email", "invalidEmail", "이메일 형식에 맞지 않습니다.");
        }

        if(signUpRequestDto.getPassword().length() < 8){
            errors.rejectValue("password", "invalidPassword", "비밀번호는 8자 이상이어야 합니다.");
        }else if(signUpRequestDto.getPassword().length() >30){
            errors.rejectValue("password", "invalidPassword", "비밀번호는 30자 이내여야 합니다.");
        }else if((signUpRequestDto.getPassword().contains(" "))){
            errors.rejectValue("password", "invalidPassword","비밀번호에 공백은 포함될 수 없습니다.");
        }

        if(accountRepository.existsByUserId(signUpRequestDto.getUserId())){
            errors.rejectValue("userId", "userIdAlreadyExists", "이미 사용중인 아이디 입니다.");
        }

        if(accountRepository.existsByNickname(signUpRequestDto.getNickname())){
            errors.rejectValue("nickname", "nicknameAlreadyExists", "이미 사용중인 닉네임 입니다.");
        }
        
        if(accountRepository.existsByEmail(signUpRequestDto.getEmail())){
            errors.rejectValue("email", "invalidEmail", "이미 사용중인 이메일 입니다.");
        }
    }
}
