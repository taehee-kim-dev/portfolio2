package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.dto.request.ProfileUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class ProfileUpdateRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(ProfileUpdateRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        ProfileUpdateRequestDto profileUpdateRequestDto = (ProfileUpdateRequestDto)o;

        if(profileUpdateRequestDto.getBio().length() > 30){
            errors.rejectValue("bio", "tooLongBio", "30자 이내여야 합니다.");
        }

        if(profileUpdateRequestDto.getOccupation().length() > 15){
            errors.rejectValue("occupation", "tooLongOccupation", "15자 이내여야 합니다.");
        }

        if(profileUpdateRequestDto.getLocation().length() > 15){
            errors.rejectValue("location", "tooLongLocation", "15자 이내여야 합니다.");
        }
        
    }
}
