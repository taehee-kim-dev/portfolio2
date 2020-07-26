package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.dto.request.PasswordUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class PasswordUpdateRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(PasswordUpdateRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        PasswordUpdateRequestDto passwordUpdateRequestDto = (PasswordUpdateRequestDto)o;

        String passwordPattern = "^[\\S0-9]{8,50}$";

        if(!(passwordUpdateRequestDto.getNewPassword().matches(passwordPattern))){
            errors.rejectValue("newPassword", "invalidFormatNewPassword",
                    "8~50자의 공백을 제외한 문자, 숫자만 사용 가능합니다.");
        }else if(!passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getNewPasswordConfirm())) {
            errors.rejectValue("newPasswordConfirm", "notSamePassword", "위의 비밀번호와 같지 않습니다.");
        }
        
    }
}
