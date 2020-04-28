package portfolio2.validator.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.dto.account.SendEmailLoginLinkRequestDto;

@Component
@RequiredArgsConstructor
public class SendEmailLoginLinkRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SendEmailLoginLinkRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        SendEmailLoginLinkRequestDto sendEmailLoginLinkRequestDto = (SendEmailLoginLinkRequestDto)o;
        
        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        if(!(sendEmailLoginLinkRequestDto.getEmail().matches(emailPattern))){
            errors.rejectValue("email", "invalidEmail", new Object[]{sendEmailLoginLinkRequestDto.getEmail()}, "이메일 형식에 맞지 않습니다.");
        }
    }
}
