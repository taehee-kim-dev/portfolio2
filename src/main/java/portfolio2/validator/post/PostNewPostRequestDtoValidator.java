package portfolio2.validator.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.dto.post.PostNewPostRequestDto;

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

        if(postNewPostRequestDto.getTitle().length() < 5){
            errors.rejectValue("title", "tooShortPostTitle","제목은 5자 이상이어야 합니다. (현재 : " + postNewPostRequestDto.getTitle().length() + "자)");
        }else if(postNewPostRequestDto.getTitle().length() > 50){
            errors.rejectValue("title", "tooLongPostTitle","제목은 50자 이내여야 합니다. (현재 : " + postNewPostRequestDto.getTitle().length() + "자)");
        }
    }
}
