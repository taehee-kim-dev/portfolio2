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

        String nicknamePattern = "^[a-zA-Z가-힣0-9]+$";

        String nickname = accountNicknameUpdateRequestDto.getNickname();

        if(nickname.length() < 3){
            errors.rejectValue("nickname", "tooShortNickname", "닉네임은 3자 이상이어야 합니다.");
        }else if(nickname.length() >15){
            errors.rejectValue("nickname", "tooLongNickname", "닉네임은 15자 이내여야 합니다.");
        }else if(!(nickname.matches(nicknamePattern))){
            errors.rejectValue("nickname", "invalidFormatNickname", "형식에 맞지 않는 닉네임 입니다.");
        }else if(accountRepository.existsByNickname(nickname)){
            errors.rejectValue("nickname", "nicknameAlreadyExists", "이미 사용중인 닉네임 입니다.");
        }
        
    }
}
