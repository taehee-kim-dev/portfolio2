//package portfolio2.controller.account;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import portfolio2.controller.account.result.EmailVerificationLinkCheckResult;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.SessionAccount;
//import portfolio2.service.account.EmailVerificationService;
//
//import java.util.HashMap;
//
//import static portfolio2.config.UrlAndViewName.*;
//
//@Controller
//@RequiredArgsConstructor
//public class EmailVerificationController {
//
//    private final EmailVerificationService emailVerificationService;
//
//    // 이메일 인증 링크 확인
//    // 인증 되면, 로그인 유무와 관계없이 무조건 현재 인증 링크에 해당하는 계정으로 로그인
//    @GetMapping(CHECK_EMAIL_VERIFICATION_LINK_URL)
//    public String checkEmailVerificationLink(String email, String token, Model model){
//
//        boolean isValidLink = emailVerificationService.checkEmailVerificationLink(email, token);
//
//        if(!isValidLink){
//            model.addAttribute("error", "inValidLink");
//            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
//        }
//
//        Account accountInDb = emailVerificationService.emailVerifyAndLogIn(email);
//
//        model.addAttribute("sessionAccount", accountInDb);
//        model.addAttribute("nickname", accountInDb.getNickname());
//        model.addAttribute("userId", accountInDb.getUserId());
//        model.addAttribute("email", accountInDb.getVerifiedEmail());
//
//        return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
//    }
//
//
//    @GetMapping(SEND_EMAIL_VERIFICATION_LINK_URL)
//    public String sendEmailVerificationLink(@SessionAccount Account sessionAccount,
//                                            Model model,
//                                            RedirectAttributes redirectAttributes) {
//
//        if (!emailVerificationService.canSendEmailVerificationLink(Account sessionAccount)) {
//            model.addAttribute("error", "인증 이메일은 12시간동안 5번만 보낼 수 있습니다.");
//            return EMAIL_VERIFICATION_RESULT_VIEW_NAME;
//        }
//
//        redirectAttributes.addFlashAttribute("message", "인증 이메일을 보냈습니다.");
//        return REDIRECT + ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
//    }
//}