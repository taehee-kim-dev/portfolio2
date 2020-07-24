package portfolio2.module.test.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;

import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@Controller
public class TestController {

    private final String EMAIL_VERIFICATION_SUCCESS_VIEW_NAME = "email/email-verification-view/email-verification-success";
    private final String ERROR_VIEW_NAME = "error/error-view";

    private void addEmailVerifiedAccountInformationToModel(@SessionAccount Account sessionAccount, Model model) {
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        // 일단 메일 인증된 계정정보 모델에 담음.
        model.addAttribute("nickname", "testNickname");
        model.addAttribute("userId", "testUserId");
        model.addAttribute("email", "test@email.com");
    }

    // email verification
    // 로그아웃 상태
    @GetMapping("/test-view/email-verification-success/not-logged-in")
    public String viewTestForEmailVerificationSuccessNotLoggedIn(@SessionAccount Account sessionAccount, Model model){
        addEmailVerifiedAccountInformationToModel(sessionAccount, model);
        model.addAttribute("isEmailVerifiedAccountLoggedIn", false);
        return EMAIL_VERIFICATION_SUCCESS_VIEW_NAME;
    }

    // 로그인 상태

    @GetMapping("/test-view/email-verification-success/owner-logged-in")
    public String viewTestForEmailVerificationSuccessOwnerLoggedIn(@SessionAccount Account sessionAccount, Model model){
        addEmailVerifiedAccountInformationToModel(sessionAccount, model);
        model.addAttribute("isEmailVerifiedAccountLoggedIn", true);
        return EMAIL_VERIFICATION_SUCCESS_VIEW_NAME;
    }

    @GetMapping("/test-view/email-verification-success/not-owner-logged-in")
    public String viewTestForEmailVerificationSuccessNotOwnerLoggedIn(@SessionAccount Account sessionAccount, Model model){
        addEmailVerifiedAccountInformationToModel(sessionAccount, model);
        model.addAttribute("isEmailVerifiedAccountLoggedIn", false);
        return EMAIL_VERIFICATION_SUCCESS_VIEW_NAME;
    }

    @GetMapping("/test-view/email-verification-request")
    public String viewTestForEmailVerificationRequest(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("email", "test@email.com");
        return "email/email-verification-view/email-verification-request";
    }

    // error
    @GetMapping("/test-view/error/cannot-send-email")
    public String viewTestForCannotSendEmailError(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("errorTitle", "인증 이메일 전송 에러");
        model.addAttribute("errorContent", "이메일 인증 이메일은 12시간동안 5번까지만 보낼 수 있습니다.");
        return ERROR_VIEW_NAME;
    }

    @GetMapping("/test-view/error/user-not-found")
    public String viewTestForUserNotFoundError(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("errorTitle", "사용자 조회 에러");
        model.addAttribute("errorContent", "존재하지 않는 사용자 입니다.");
        return ERROR_VIEW_NAME;
    }

    @GetMapping("/test-view/error/not-author")
    public String viewTestForNotAuthorError(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("errorTitle", "글 수정 권한 없음");
        model.addAttribute("errorContent", "현재 로그인 되어있는 계정이 수정하고자 하는 글의 작성자 계정이 아닙니다.");
        return ERROR_VIEW_NAME;
    }


}
