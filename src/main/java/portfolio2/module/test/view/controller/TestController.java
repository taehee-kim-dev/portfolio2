package portfolio2.module.test.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;

import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@Controller
public class TestController {

    @GetMapping("/test-view")
    public String viewTest(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("errorTitle", "인증 이메일 전송 에러");
        model.addAttribute("errorContent", "이메일 인증 이메일은 12시간동안 5번까지만 보낼 수 있습니다.");
        return "error/error-view.html";
    }
}
