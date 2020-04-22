package portfolio2.web.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.web.dto.PasswordUpdateRequestDto;
import portfolio2.web.dto.ProfileUpdateRequestDto;

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

        if(passwordUpdateRequestDto.getNewPassword().length() < 8){
            errors.rejectValue("newPassword", "invalidNewPassword", new Object[]{passwordUpdateRequestDto.getNewPassword()}, "비밀번호는 8자 이상이어야 합니다.");
        }else if(passwordUpdateRequestDto.getNewPassword().length() >30){
            errors.rejectValue("newPassword", "invalidNewPassword", new Object[]{passwordUpdateRequestDto.getNewPassword()}, "비밀번호는 30자 이내여야 합니다.");
        }else if((passwordUpdateRequestDto.getNewPassword().contains(" "))){
            errors.rejectValue("newPassword", "invalidNewPassword", new Object[]{passwordUpdateRequestDto.getNewPassword()}, "비밀번호에 공백은 포함될 수 없습니다.");
        }

        if(!passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getNewPasswordConfirm())){
            errors.rejectValue("newPasswordConfirm", "invalidNewPasswordConfirm", new Object[]{passwordUpdateRequestDto.getNewPasswordConfirm()}, "위의 비밀번호와 같지 않습니다.");
        }
        
    }
}
