package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.SessionAccount;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;
import portfolio2.validator.account.SignUpRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.config.UrlAndViewName.*;
import static portfolio2.config.UrlAndViewName.HOME_URL;

@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpRequestDtoValidator signUpRequestDtoValidator;
    private final SignUpService signUpService;

    @InitBinder("signUpRequestDto")
    public void initBinderForSignUpRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpRequestDtoValidator);
    }

    @GetMapping(SIGN_UP_URL)
    public String showSignUpPage(@SessionAccount Account sessionAccount, Model model) {

        if(sessionAccount != null){
            return REDIRECT + HOME_URL;
        }

        // name값을 생략하면, 객체 이름의 camel 케이스를 이름으로 줌.
        // model.addAttribute("signUpRequestDto", new SignUpRequestDto()); 와 동일.
        model.addAttribute(new SignUpRequestDto());
        return SIGN_UP_VIEW_NAME;
    }

    /*
        복합 객체(여러 필드를 갖고 있는 객체)를 받아올 때는
        원래 @ModelAttribute를 써서 받아와야 한다.
        @Valid @ModelAttribute SignUpRequestDto signupForm
        생략 가능.
    * */
    @PostMapping(SIGN_UP_URL)
    public String signUp(@SessionAccount Account sessionAccount,
                         @Valid @ModelAttribute SignUpRequestDto signUpRequestDto,
                         Errors errors,
                         Model model) {

        if(sessionAccount != null){
            return REDIRECT + HOME_URL;
        }

        if (errors.hasErrors()) {
            // Validation에서 error가 발생하면,
            // form을 다시 보여준다.
            model.addAttribute(signUpRequestDto);
            return SIGN_UP_VIEW_NAME;
        }

        signUpService.signUp(signUpRequestDto);

        model.addAttribute("email", signUpRequestDto.getEmail());

        return EMAIL_VERIFICATION_REQUEST_VIEW_NAME;
    }
}