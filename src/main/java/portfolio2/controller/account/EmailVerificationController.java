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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.SessionAccount;
import portfolio2.dto.account.profileupdate.AccountEmailUpdateRequestDto;
import portfolio2.dto.account.profileupdate.AccountNicknameUpdateRequestDto;
import portfolio2.service.account.EmailVerificationService;
import portfolio2.validator.account.profile.update.AccountEmailUpdateRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.config.UrlAndViewName.*;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 링크 확인
    // 인증 되면, 로그인 유무와 관계없이 무조건 현재 인증 링크에 해당하는 계정으로 로그인
    @GetMapping(CHECK_EMAIL_VERIFICATION_LINK_URL)
    public String checkEmailVerificationLink(String email, String token, Model model){

        boolean isValidLink = emailVerificationService.checkEmailVerificationLink(email, token);

        if(!isValidLink){
            model.addAttribute("invalidLinkError", "invalidLinkError");
            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
        }

        Account sessionAccount = emailVerificationService.emailVerifyAndLogIn();

        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("nickname", sessionAccount.getNickname());
        model.addAttribute("userId", sessionAccount.getUserId());
        model.addAttribute("email", sessionAccount.getVerifiedEmail());

        return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
    }
}