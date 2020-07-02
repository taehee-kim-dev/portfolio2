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
import portfolio2.dto.account.EmailVerificationRequestDto;
import portfolio2.service.account.EmailVerificationService;
import portfolio2.service.account.SignUpService;
import portfolio2.validator.account.EmailVerificationRequestDtoValidator;
import portfolio2.validator.account.SignUpRequestDtoValidator;

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
    // 인증 되면, 로그인 유무와 관계없이 무조건 현재 인증 링크에 해당하는 계정으로 로그인
    @GetMapping(CHECK_EMAIL_VERIFICATION_LINK_URL)
    public String checkEmailVerificationLink(@Valid @ModelAttribute
                                                         EmailVerificationRequestDto
                                                         emailVerificationRequestDto,
                                             Errors errors,
                                             Model model){

        System.out.println("***");
        System.out.println(emailVerificationRequestDto.getEmail());
        System.out.println(emailVerificationRequestDto.getToken());

        if(errors.hasErrors()){
            model.addAttribute("invalidLinkError", "invalidLinkError");
            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
        }


//        boolean isValidLink
//                = emailVerificationService.checkEmailVerificationLink(
//                        emailVerificationRequestDto.getEmail(),
//                        emailVerificationRequestDto.getToken()
//                );
//
//        if(!isValidLink){
//            model.addAttribute("invalidLinkError", "invalidLinkError");
//            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
//        }

        Account sessionAccount = emailVerificationService.emailVerifyAndLogIn();

        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("nickname", sessionAccount.getNickname());
        model.addAttribute("userId", sessionAccount.getUserId());
        model.addAttribute("email", sessionAccount.getVerifiedEmail());

        return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
    }
}