package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.AccountNicknameUpdateRequestDto;


@Component
@RequiredArgsConstructor
public class AccountNicknameUpdateRequestDtoValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountNicknameUpdateRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto = (AccountNicknameUpdateRequestDto)o;

        String nicknamePattern = "^[a-zA-Z가-힣0-9-_.]{2,20}$";

        String nickname = accountNicknameUpdateRequestDto.getNickname();

        if(!(nickname.matches(nicknamePattern))){
            errors.rejectValue("nickname", "invalidFormatNickname",
                    "2~20자의 영문, 한글(단순 모음, 자음 제외), 숫자와 특수문자 (-), (_), (.)만 사용 가능합니다.");
        }else if(accountRepository.existsByNickname(nickname)){
            errors.rejectValue("nickname", "nicknameAlreadyExists",
                    "이미 사용중인 닉네임 입니다.");
        }
        
    }
}
