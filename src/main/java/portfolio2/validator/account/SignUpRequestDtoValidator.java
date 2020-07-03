package portfolio2.validator.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.SignUpRequestDto;

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


        String userId = signUpRequestDto.getUserId();

        if(userId.length() < 5){
            errors.rejectValue("userId", "tooShortUserId","아이디는 5자 이상이어야 합니다.");
        }else if(userId.length() > 20){
            errors.rejectValue("userId", "tooLongUserId","아이디는 20자 이내여야 합니다.");
        }else if(!(userId.matches(userIdPattern))){
            errors.rejectValue("userId", "invalidFormatUserId", "형식에 맞지 않는 아이디 입니다.");
        }else if(accountRepository.existsByUserId(userId)){
            errors.rejectValue("userId", "userIdAlreadyExists", "이미 사용중인 아이디 입니다.");
        }


        String nickname = signUpRequestDto.getNickname();

        if(nickname.length() < 3){
            errors.rejectValue("nickname", "tooShortNickname", "닉네임은 3자 이상이어야 합니다.");
        }else if(nickname.length() >15){
            errors.rejectValue("nickname", "tooLongNickname", "닉네임은 15자 이내여야 합니다.");
        }else if(!(nickname.matches(nicknamePattern))){
            errors.rejectValue("nickname", "invalidFormatNickname", "형식에 맞지 않는 닉네임 입니다.");
        }else if(accountRepository.existsByNickname(nickname)){
            errors.rejectValue("nickname", "nicknameAlreadyExists", "이미 사용중인 닉네임 입니다.");
        }


        String email = signUpRequestDto.getEmail();

        if(!(email.matches(emailPattern))){
            errors.rejectValue("email", "invalidFormatEmail", "이메일 형식에 맞지 않습니다.");
        }else if(accountRepository.existsByVerifiedEmail(email)
                || accountRepository.existsByEmailWaitingToBeVerified(email)){
            errors.rejectValue("email", "emailAlreadyExists", "이미 사용중인 이메일 입니다.");
        }

        String password = signUpRequestDto.getPassword();

        if(password.length() < 8){
            errors.rejectValue("password", "tooShortPassword", "비밀번호는 8자 이상이어야 합니다.");
        }else if(password.length() >30){
            errors.rejectValue("password", "tooLongPassword", "비밀번호는 30자 이내여야 합니다.");
        }else if((password.contains(" "))){
            errors.rejectValue("password", "invalidFormatPassword","비밀번호에 공백은 포함될 수 없습니다.");
        }

    }
}
