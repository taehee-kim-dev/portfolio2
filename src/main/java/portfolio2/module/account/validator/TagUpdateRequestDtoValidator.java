package portfolio2.module.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.AccountEmailUpdateRequestDto;
import portfolio2.module.account.dto.request.TagUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class TagUpdateRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(TagUpdateRequestDto.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        TagUpdateRequestDto tagUpdateRequestDto = (TagUpdateRequestDto)o;

        String tagPattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9`~!@#$%^&*(\\\\)_+\\-=\\[\\];',./{}|:\"<>? ]{0,20}$";

        if(!(tagUpdateRequestDto.getTagTitle().matches(tagPattern))){
            errors.rejectValue("tagTitle", "invalidFormatTagTitle",
                    "태그의 정규식에 어긋나며, 비정상적인 요청입니다.");
        }

    }
}
