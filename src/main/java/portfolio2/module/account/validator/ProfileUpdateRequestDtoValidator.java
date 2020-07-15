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

        if(profileUpdateRequestDto.getBio().length() > 35){
            errors.rejectValue("bio", "tooLongBio", "한 줄 소개는 35자 이내여야 합니다.");
        }

        if(profileUpdateRequestDto.getOccupation().length() > 20){
            errors.rejectValue("occupation", "tooLongOccupation", "직업은 20자 이내여야 합니다.");
        }

        if(profileUpdateRequestDto.getLocation().length() > 20){
            errors.rejectValue("location", "tooLongLocation", "지역은 20자 이내여야 합니다.");
        }
        
    }
}
