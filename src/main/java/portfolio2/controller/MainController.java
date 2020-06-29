package portfolio2.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.SessionAccount;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@SessionAccount Account sessionAccount, Model model){
        if(sessionAccount != null){
            model.addAttribute("sessionAccount", sessionAccount);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(@SessionAccount Account sessionAccount){
        if(sessionAccount != null){
            return "redirect:/";
        }
        return "account/login";
    }
}

