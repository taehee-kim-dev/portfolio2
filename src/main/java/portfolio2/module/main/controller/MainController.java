package portfolio2.module.main.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;

import static portfolio2.module.main.config.UrlAndViewNameAboutMain.HOME_URL;
import static portfolio2.module.main.config.UrlAndViewNameAboutMain.HOME_VIEW_NAME;
import static portfolio2.module.main.config.VariableNameAboutMain.SESSION_ACCOUNT;

@RequiredArgsConstructor
@Controller
public class MainController {

    @GetMapping(HOME_URL)
    public String home(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        return HOME_VIEW_NAME;
    }
}

