package portfolio2.validator.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.dto.request.post.PostRequestDto;

@Component
@RequiredArgsConstructor
public class PostRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PostRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostRequestDto postRequestDto = (PostRequestDto)target;

        if(postRequestDto.getTitle().length() == 0){
            errors.rejectValue("title", "emptyTitle","제목을 작성해 주세요.");
        }

        if(postRequestDto.getContent().length() == 0){
            errors.rejectValue("content", "emptyContent","내용을 작성해 주세요.");
        }
    }
}
