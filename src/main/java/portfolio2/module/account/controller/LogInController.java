package portfolio2.module.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;

import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.LOGIN_URL;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.LOGIN_VIEW_NAME;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.HOME_URL;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.REDIRECT;

@RequiredArgsConstructor
@Controller
public class LogInController {

    // 로그인 되어있으면 안됨.
    @GetMapping(LOGIN_URL)
    public String login(@SessionAccount Account sessionAccount){
        if(sessionAccount != null){
            return REDIRECT + HOME_URL;
        }
        return LOGIN_VIEW_NAME;
    }
}
