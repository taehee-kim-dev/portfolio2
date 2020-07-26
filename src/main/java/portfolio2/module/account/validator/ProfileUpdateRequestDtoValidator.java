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

        String bioPattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9`~!@#$%^&*(\\\\)_+\\-=\\[\\];',./{}|:\"<>? ]{0,30}$";
        String occupationAndLocationPattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9`~!@#$%^&*(\\\\)_+\\-=\\[\\];',./{}|:\"<>? ]{0,15}$";
        ProfileUpdateRequestDto profileUpdateRequestDto = (ProfileUpdateRequestDto)o;

        if(!(profileUpdateRequestDto.getBio().matches(bioPattern))){
            errors.rejectValue("bio", "invalidBio", "30자 이내의 문자, 숫자만 사용 가능합니다.");
        }

        if(!(profileUpdateRequestDto.getOccupation().matches(occupationAndLocationPattern))){
            errors.rejectValue("occupation", "invalidOccupation", "15자 이내의 문자, 숫자만 사용 가능합니다.");
        }

        if(!(profileUpdateRequestDto.getLocation().matches(occupationAndLocationPattern))){
            errors.rejectValue("location", "invalidLocation", "15자 이내의 문자, 숫자만 사용 가능합니다.");
        }
        
    }
}
