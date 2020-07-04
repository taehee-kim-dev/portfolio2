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
import portfolio2.dto.request.account.EmailVerificationRequestDto;
import portfolio2.dto.response.account.EmailVerificationResponseDto;
import portfolio2.service.account.EmailVerificationService;
import portfolio2.validator.account.EmailVerificationRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.controller.config.UrlAndViewName.*;

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

        EmailVerificationResponseDto emailVerificationResponseDto
                = emailVerificationService.verifyEmailAndUpdateSessionIfLoggedInByEmailVerifiedAccount(
                        emailVerificationRequestDto, sessionAccount);

        // 일단 메일 인증된 계정정보 모델에 담음.
        model.addAttribute("nickname", emailVerificationResponseDto.getEmailVerifiedAccountInDb().getNickname());
        model.addAttribute("userId", emailVerificationResponseDto.getEmailVerifiedAccountInDb().getUserId());
        model.addAttribute("email", emailVerificationResponseDto.getEmailVerifiedAccountInDb().getVerifiedEmail());

        // 이메일 인증된 계정으로 로그인 되어있는 경우, 업데이트된 세션으로 저장.
        if(emailVerificationResponseDto.isEmailVerifiedAccountLoggedIn()){
            model.addAttribute(SESSION_ACCOUNT, emailVerificationResponseDto.getUpdatedSessionAccount());
        }else{
            // 그 외의 경우는 현재 세션으로 저장.
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        }
        // 이메일 인증된 계정으로 현재 로그인 되어있는지 상태값 모델에 담아 전달.
        model.addAttribute("isEmailVerifiedAccountLoggedIn", emailVerificationResponseDto.isEmailVerifiedAccountLoggedIn());
        return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
    }
}