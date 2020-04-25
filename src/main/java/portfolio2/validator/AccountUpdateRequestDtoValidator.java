package portfolio2.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.AccountUpdateRequestDto;
import portfolio2.dto.PasswordUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class AccountUpdateRequestDtoValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountUpdateRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        AccountUpdateRequestDto accountUpdateRequestDto = (AccountUpdateRequestDto)o;

        String nicknamePattern = "^[a-zA-Z가-힣0-9]+$";

        if(accountUpdateRequestDto.getNickname().length() < 3){
            errors.rejectValue("nickname", "invalidNickname", new Object[]{accountUpdateRequestDto.getNickname()}, "닉네임은 3자 이상이어야 합니다.");
        }else if(accountUpdateRequestDto.getNickname().length() >15){
            errors.rejectValue("nickname", "invalidNickname", new Object[]{accountUpdateRequestDto.getNickname()}, "닉네임은 15자 이내여야 합니다.");
        }else if(!(accountUpdateRequestDto.getNickname().matches(nicknamePattern))){
            errors.rejectValue("nickname", "invalidNickname", new Object[]{accountUpdateRequestDto.getNickname()}, "형식에 맞지 않는 닉네임 입니다.");
        }

        if(accountRepository.existsByNickname(accountUpdateRequestDto.getNickname())){
            errors.rejectValue("nickname", "invalidNickname", new Object[]{accountUpdateRequestDto.getNickname()}, "이미 사용중인 닉네임 입니다.");
        }
        
    }
}
