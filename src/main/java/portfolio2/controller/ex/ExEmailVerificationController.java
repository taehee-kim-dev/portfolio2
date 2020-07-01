//package portfolio2.controller.ex;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.SessionAccount;
//import portfolio2.dto.account.profileupdate.AccountEmailUpdateRequestDto;
//import portfolio2.dto.account.profileupdate.AccountNicknameUpdateRequestDto;
//import portfolio2.service.account.EmailVerificationService;
//import portfolio2.validator.account.profile.update.AccountEmailUpdateRequestDtoValidator;
//
//import javax.validation.Valid;
//
//import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
//import static portfolio2.config.UrlAndViewName.*;
//
//@Controller
//@RequiredArgsConstructor
//public class ExEmailVerificationController {
//
//    private final EmailVerificationService emailVerificationService;
//    private final AccountEmailUpdateRequestDtoValidator accountEmailUpdateRequestDtoValidator;
//
//    @InitBinder("accountEmailUpdateRequestDto")
//    public void initBinderForAccountEmailUpdateRequestDtoValidator(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(accountEmailUpdateRequestDtoValidator);
//    }
//
//    // 이메일 인증 링크 확인
//    // 인증 되면, 로그인 유무와 관계없이 무조건 현재 인증 링크에 해당하는 계정으로 로그인
//    @GetMapping(CHECK_EMAIL_VERIFICATION_LINK_URL)
//    public String checkEmailVerificationLink(String email, String token, Model model){
//
//        boolean isValidLink = emailVerificationService.checkEmailVerificationLink(email, token);
//
//        if(!isValidLink){
//            model.addAttribute("invalidLinkError", "invalidLinkError");
//            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
//        }
//
//        Account sessionAccount = emailVerificationService.emailVerifyAndLogIn();
//
//        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//        model.addAttribute("nickname", sessionAccount.getNickname());
//        model.addAttribute("userId", sessionAccount.getUserId());
//        model.addAttribute("email", sessionAccount.getVerifiedEmail());
//
//        return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
//    }
//
//    // 로그인 상태에서만 보낼 수 있다.
//    @PostMapping(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//    public String sendEmailVerificationEmail(@SessionAccount Account sessionAccount,
//                                             @Valid @ModelAttribute AccountEmailUpdateRequestDto accountEmailUpdateRequestDto,
//                                             Errors errors, Model model,
//                                             RedirectAttributes redirectAttributes) {
//
//        if(errors.hasErrors()){
//            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//            AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto = new AccountNicknameUpdateRequestDto();
//            accountNicknameUpdateRequestDto.setNickname(sessionAccount.getNickname());
//            model.addAttribute(accountEmailUpdateRequestDto);
//            model.addAttribute(accountEmailUpdateRequestDto);
//            return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
//        }
//
//        if (!emailVerificationService.canSendEmailVerificationEmail(sessionAccount)) {
//            model.addAttribute("cannotSendError", "인증 이메일은 12시간동안 5번만 보낼 수 있습니다.");
//            return CANNOT_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME;
//        }
//
//        // 보낼 수 있으면 보냄
//        emailVerificationService.sendEmailVerificationEmail(accountEmailUpdateRequestDto);
//
//        redirectAttributes.addFlashAttribute("message", "인증 이메일을 보냈습니다.");
//        return REDIRECT + ACCOUNT_SETTING_ACCOUNT_URL;
//    }
//}