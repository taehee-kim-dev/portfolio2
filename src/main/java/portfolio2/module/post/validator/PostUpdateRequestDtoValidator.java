package portfolio2.module.post.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.post.dto.PostUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class PostUpdateRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PostUpdateRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostUpdateRequestDto postUpdateRequestDto = (PostUpdateRequestDto)target;
        String postTitlePattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9`~!@#$%^&*(\\\\)_+\\-=\\[\\];',./{}|:\"<>? ]{1,50}$";
        if(postUpdateRequestDto.getTitle().length() == 0){
            errors.rejectValue("title", "emptyTitle","제목을 작성해 주세요.");
        }else if(!(postUpdateRequestDto.getTitle().matches(postTitlePattern))){
            errors.rejectValue("title", "invalidTitle", "50자 이내의 문자, 숫자만 사용 가능합니다.");
        }

        if(!isValidTag(postUpdateRequestDto)){
            errors.rejectValue("tagTitleOnPost", "invalidFormatTagTitleOnPost", "태그 정규식에 맞지 않으며, 잘못된 요청입니다.");
        }
    }

    private boolean isValidTag(PostUpdateRequestDto postUpdateRequestDto) {
        String tagPattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9`~!@#$%^&*(\\\\)_+\\-=\\[\\];',./{}|:\"<>? ]{0,20}$";
        String[] tagArray = postUpdateRequestDto.getTagTitleOnPost().split(",");
        for(String tagTitle : tagArray){
            if(!tagTitle.matches(tagPattern)){
                return false;
            }
        }
        return true;
    }
}
