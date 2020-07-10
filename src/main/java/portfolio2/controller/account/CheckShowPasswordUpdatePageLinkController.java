package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.config.SessionAccount;
import portfolio2.dto.request.account.ShowPasswordUpdatePageRequestDto;
import portfolio2.service.account.CheckShowPasswordUpdatePageLinkService;
import portfolio2.validator.email.link.ShowUpdatePasswordPageRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.controller.config.UrlAndViewName.*;

@Controller
@RequiredArgsConstructor
public class CheckShowPasswordUpdatePageLinkController {

    private final CheckShowPasswordUpdatePageLinkService checkShowPasswordUpdatePageLinkService;

    private final ShowUpdatePasswordPageRequestDtoValidator showUpdatePasswordPageRequestDtoValidator;

    @InitBinder("showPasswordUpdatePageRequestDto")
    public void initBinderForShowPasswordUpdatePageRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(showUpdatePasswordPageRequestDtoValidator);
    }

    // 비밀번호 변경 화면 로그인 상태로 보여주기
    @GetMapping(CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL)
    public String checkShowPasswordUpdatePageLink(@SessionAccount Account sessionAccount,
                                             @Valid @ModelAttribute
                                                     ShowPasswordUpdatePageRequestDto
                                                     showPasswordUpdatePageRequestDto,
                                             Errors errors,
                                             Model model){

        if(errors.hasErrors()){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute("invalidLinkError", "invalidLinkError");
            return INVALID_EMAIL_LINK_ERROR_VIEW_NAME;
        }

        // 정상 링크
        // 해당 계정으로 로그인
        checkShowPasswordUpdatePageLinkService.login(showPasswordUpdatePageRequestDto);
        return REDIRECT + ACCOUNT_SETTING_PASSWORD_URL;
    }
}