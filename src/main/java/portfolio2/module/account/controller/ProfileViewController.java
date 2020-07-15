package portfolio2.module.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.account.service.ProfileViewService;

import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@Controller
@RequiredArgsConstructor
public class ProfileViewController {

    private final ProfileViewService profileViewService;

    @GetMapping(PROFILE_VIEW_URL + "/{userId}")
    public String viewProfile(@PathVariable String userId,
                              @SessionAccount Account sessionAccount,
                              Model model){

        model.addAttribute(SESSION_ACCOUNT, sessionAccount);

        Account searchedAccount = profileViewService.findUser(userId);

        // 존재하지 않는 사용자인 경우
        if(searchedAccount == null){
            model.addAttribute("notFoundErrorTitle", "사용자 조회 에러");
            model.addAttribute("notFoundErrorContent", "존재하지 않는 사용자 입니다.");
            return NOT_FOUND_ERROR_VIEW_NAME;
        }

        // 존재하는 사용자인 경우

        // 객체 타입의 camel case를 이름으로 준다.
        // mode.addAttribute("account", byUserId)와 같음.
        model.addAttribute("searchedAccount", searchedAccount);
        model.addAttribute("isOwner", searchedAccount.equals(sessionAccount));
        return PROFILE_VIEW_VIEW_NAME;
    }

}