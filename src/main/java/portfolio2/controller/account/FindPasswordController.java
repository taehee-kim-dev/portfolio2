package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.config.SessionAccount;
import portfolio2.dto.request.account.FindPasswordRequestDto;
import portfolio2.service.account.FindPasswordService;
import portfolio2.validator.account.FindPasswordRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.controller.config.UrlAndViewNameAboutBasic.HOME_URL;
import static portfolio2.controller.config.UrlAndViewNameAboutBasic.REDIRECT;

@Controller
@RequiredArgsConstructor
public class FindPasswordController {

    private final FindPasswordRequestDtoValidator findPasswordRequestDtoValidator;
    private final FindPasswordService findPasswordService;

    @InitBinder("findPasswordRequestDto")
    public void initBinderForFindPasswordRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(findPasswordRequestDtoValidator);
    }

    // 로그인 되어있으면 안됨.
    @GetMapping(FIND_PASSWORD_URL)
    public String showFindPasswordPage(@SessionAccount Account sessionAccount, Model model){
        if(sessionAccount != null){
            return REDIRECT + HOME_URL;
        }
        model.addAttribute(new FindPasswordRequestDto());
        return FIND_PASSWORD_VIEW_NAME;
    }

    // 로그인 되어있으면 안됨.
    @PostMapping(FIND_PASSWORD_URL)
    public String sendFindPasswordEmail(@SessionAccount Account sessionAccount,
                                        @Valid @ModelAttribute FindPasswordRequestDto findPasswordRequestDto,
                                        Errors errors, Model model) {
        if(sessionAccount != null){
            return REDIRECT + HOME_URL;
        }

        if(errors.hasErrors()){
            model.addAttribute(findPasswordRequestDto);
            return FIND_PASSWORD_VIEW_NAME;
        }

        findPasswordService.sendFindPasswordEmail(findPasswordRequestDto);

        model.addAttribute("successMessage", "비밀번호 찾기 이메일을 발송했습니다.");
        model.addAttribute(findPasswordRequestDto);
        return FIND_PASSWORD_VIEW_NAME;
    }
}