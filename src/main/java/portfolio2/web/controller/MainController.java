package portfolio2.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.CurrentUser;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "index";
    }

//    @GetMapping("/login")
//    public String login(){
//        return "login";
//    }
}
