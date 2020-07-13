package portfolio2.validator.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.dto.request.post.PostNewPostRequestDto;

@Component
@RequiredArgsConstructor
public class PostNewPostRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PostNewPostRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostNewPostRequestDto postNewPostRequestDto = (PostNewPostRequestDto)target;

        if(postNewPostRequestDto.getTitle().length() == 0){
            errors.rejectValue("title", "emptyTitle","제목을 작성해 주세요.");
        }

        if(postNewPostRequestDto.getContent().length() == 0){
            errors.rejectValue("content", "emptyContent","내용을 작성해 주세요.");
        }
    }
}
