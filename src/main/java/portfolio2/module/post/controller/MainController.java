package portfolio2.module.post.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.post.PostRepository;

import static portfolio2.module.main.config.StaticVariableNamesAboutMain.*;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final PostRepository postRepository;

    @GetMapping(HOME_URL)
    public String home(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("postList", postRepository.findFirst15ByOrderByFirstWrittenDateTimeDesc());
        return HOME_VIEW_NAME;
    }
}

