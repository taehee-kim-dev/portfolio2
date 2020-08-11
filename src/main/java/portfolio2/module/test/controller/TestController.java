package portfolio2.module.test.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.test.service.TestService;

import static portfolio2.module.main.config.StaticVariableNamesAboutMain.*;
import static portfolio2.module.test.controller.config.StaticVariableNamesAboutTest.*;

@RequiredArgsConstructor
@Controller
public class TestController {

    private final String ADMIN_USER_ID = "taehee.kim.dev";
    
    private final TestService testService;

//    private final String EMAIL_VERIFICATION_SUCCESS_VIEW_NAME = "email/email-verification-view/email-verification-success";
//    private final String ERROR_VIEW_NAME = "error/error-view";
//
//    private void addEmailVerifiedAccountInformationToModel(@SessionAccount Account sessionAccount, Model model) {
//        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//        // 일단 메일 인증된 계정정보 모델에 담음.
//        model.addAttribute("nickname", "testNickname");
//        model.addAttribute("userId", "testUserId");
//        model.addAttribute("email", "test@email.com");
//    }
//
//    // email verification
//    // 로그아웃 상태
//    @GetMapping("/test/email-verification-success/not-logged-in")
//    public String viewTestForEmailVerificationSuccessNotLoggedIn(@SessionAccount Account sessionAccount, Model model){
//        addEmailVerifiedAccountInformationToModel(sessionAccount, model);
//        model.addAttribute("isEmailVerifiedAccountLoggedIn", false);
//        return EMAIL_VERIFICATION_SUCCESS_VIEW_NAME;
//    }
//
//    // 로그인 상태
//
//    @GetMapping("/test/email-verification-success/owner-logged-in")
//    public String viewTestForEmailVerificationSuccessOwnerLoggedIn(@SessionAccount Account sessionAccount, Model model){
//        addEmailVerifiedAccountInformationToModel(sessionAccount, model);
//        model.addAttribute("isEmailVerifiedAccountLoggedIn", true);
//        return EMAIL_VERIFICATION_SUCCESS_VIEW_NAME;
//    }
//
//    @GetMapping("/test/email-verification-success/not-owner-logged-in")
//    public String viewTestForEmailVerificationSuccessNotOwnerLoggedIn(@SessionAccount Account sessionAccount, Model model){
//        addEmailVerifiedAccountInformationToModel(sessionAccount, model);
//        model.addAttribute("isEmailVerifiedAccountLoggedIn", false);
//        return EMAIL_VERIFICATION_SUCCESS_VIEW_NAME;
//    }
//
//    @GetMapping("/test/email-verification-request")
//    public String viewTestForEmailVerificationRequest(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//        model.addAttribute("email", "test@email.com");
//        return "email/email-verification-view/email-verification-request";
//    }
//
//    // error
//    @GetMapping("/test/error/cannot-send-email")
//    public String viewTestForCannotSendEmailError(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//        model.addAttribute("errorTitle", "인증 이메일 전송 에러");
//        model.addAttribute("errorContent", "이메일 인증 이메일은 12시간동안 5번까지만 보낼 수 있습니다.");
//        return ERROR_VIEW_NAME;
//    }
//
//    @GetMapping("/test/error/user-not-found")
//    public String viewTestForUserNotFoundError(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//        model.addAttribute("errorTitle", "사용자 조회 에러");
//        model.addAttribute("errorContent", "존재하지 않는 사용자 입니다.");
//        return ERROR_VIEW_NAME;
//    }
//
//    @GetMapping("/test/error/not-author")
//    public String viewTestForNotAuthorError(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
//        model.addAttribute("errorTitle", "글 수정 권한 없음");
//        model.addAttribute("errorContent", "현재 로그인 되어있는 계정이 수정하고자 하는 글의 작성자 계정이 아닙니다.");
//        return ERROR_VIEW_NAME;
//    }

    @GetMapping("/test/post-randomly/{totalNumberOfPost}")
    public String postRandomly(@SessionAccount Account sessionAccount,
                               @PathVariable int totalNumberOfPost, Model model){

        if (validationForTest(sessionAccount, model)) return ERROR_VIEW_NAME;

        testService.postRandomly(totalNumberOfPost);
        model.addAttribute(TEST_TITLE, "테스트 완료");
        model.addAttribute(TEST_CONTENT, "테스트 글 작성 완료");
        return TEST_SUCCESS_VIEW_NAME;
    }

    private boolean validationForTest(Account sessionAccount, Model model) {
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        if (!sessionAccount.getUserId().equals(ADMIN_USER_ID)) {
            model.addAttribute(ERROR_TITLE, "테스트 에러");
            model.addAttribute(ERROR_CONTENT, "테스트 권한이 없습니다.");
            return true;
        }

        if (!testService.allAccountsExist()) {
            model.addAttribute(ERROR_TITLE, "테스트 에러");
            model.addAttribute(ERROR_CONTENT, "테스트에 필요한 모든 계정이 회원가입되어 있지 않습니다.");
            return true;
        }
        return false;
    }

    @GetMapping("/test/add-tags-to-posts-randomly")
    public String addTagsToPostsRandomly(@SessionAccount Account sessionAccount, Model model){
        if (validationForTest(sessionAccount, model)) return ERROR_VIEW_NAME;

        testService.addTagsToPostsRandomly();
        model.addAttribute(TEST_TITLE, "테스트 완료");
        model.addAttribute(TEST_CONTENT, "테스트 글 작성 완료");
        return TEST_SUCCESS_VIEW_NAME;
    }
}
