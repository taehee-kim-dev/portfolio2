package portfolio2.module.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.account.dto.request.ShowPasswordUpdatePageRequestDto;
import portfolio2.module.account.service.CheckShowPasswordUpdatePageLinkService;
import portfolio2.module.account.validator.ShowUpdatePasswordPageRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.*;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.ERROR_VIEW_NAME;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

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
            model.addAttribute(ERROR_TITLE, "링크 에러");
            model.addAttribute(ERROR_CONTENT, "유효하지 않은 링크입니다.");
            return ERROR_VIEW_NAME;
        }

        // 정상 링크
        // 해당 계정으로 로그인
        checkShowPasswordUpdatePageLinkService.login(showPasswordUpdatePageRequestDto);
        return REDIRECT + ACCOUNT_SETTING_PASSWORD_URL;
    }
}