package portfolio2.validator.account.profileupdate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.dto.account.profileupdate.ProfileUpdateRequestDto;

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
            errors.rejectValue("bio", "invalidBio", new Object[]{profileUpdateRequestDto.getBio()}, "한 줄 소개는 35자 이내여야 합니다.");
        }

        if(profileUpdateRequestDto.getLocation().length() > 20){
            errors.rejectValue("location", "invalidLocation", new Object[]{profileUpdateRequestDto.getLocation()}, "거주 지역은 20자 이내여야 합니다.");
        }

        if(profileUpdateRequestDto.getOccupation().length() > 20){
            errors.rejectValue("occupation", "invalidOccupation", new Object[]{profileUpdateRequestDto.getOccupation()}, "직업은 20자 이내여야 합니다.");
        }
        
    }
}
