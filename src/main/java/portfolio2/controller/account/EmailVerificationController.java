package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.config.SessionAccount;
import portfolio2.dto.account.EmailVerificationRequestDto;
import portfolio2.service.account.EmailVerificationService;
import portfolio2.validator.account.EmailVerificationRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.config.UrlAndViewName.*;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    private final EmailVerificationRequestDtoValidator emailVerificationRequestDtoValidator;

    @InitBinder("emailVerificationRequestDto")
    public void initBinderForEmailVerificationRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(emailVerificationRequestDtoValidator);
    }

    // 이메일 인증 링크 확인
    @GetMapping(CHECK_EMAIL_VERIFICATION_LINK_URL)
    public String checkEmailVerificationLink(@SessionAccount Account sessionAccount,
                                             @Valid @ModelAttribute
                                                         EmailVerificationRequestDto
                                                         emailVerificationRequestDto,
                                             Errors errors,
                                             Model model){

        if(errors.hasErrors() || emailVerificationRequestDto == null){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute("invalidLinkError", "invalidLinkError");
            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
        }

        Account verifiedAccount = emailVerificationService.emailVerifyAndLogInIfLoggedInByOwnAccount(emailVerificationRequestDto, sessionAccount);
        boolean isOwnerLoggedIn = false;
        if(sessionAccount != null){
            isOwnerLoggedIn = sessionAccount.getUserId().equals(verifiedAccount.getUserId());
        }else{
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        }

        if(isOwnerLoggedIn){
            model.addAttribute(SESSION_ACCOUNT, verifiedAccount);
        }else{
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        }

        model.addAttribute("isOwnerLoggedIn", isOwnerLoggedIn);
        model.addAttribute("nickname", verifiedAccount.getNickname());
        model.addAttribute("userId", verifiedAccount.getUserId());
        model.addAttribute("email", verifiedAccount.getVerifiedEmail());

        return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
    }
}