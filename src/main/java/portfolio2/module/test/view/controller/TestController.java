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
        model.addAttribute("email", sessionAccount.getEmailWaitingToBeVerified());
        return "email/email-verification-view/email-verification-request";
    }
}
