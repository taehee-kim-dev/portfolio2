package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.config.SessionAccount;
import portfolio2.service.account.ProfileViewService;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.controller.config.UrlAndViewNameAboutAccount.*;

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
            model.addAttribute("notFoundError", "존재하지 않는 사용자 입니다.");
            return PROFILE_VIEW_NOT_FOUND_ERROR_VIEW_NAME;
        }

        // 존재하는 사용자인 경우

        // 객체 타입의 camel case를 이름으로 준다.
        // mode.addAttribute("account", byUserId)와 같음.
        model.addAttribute("searchedAccount", searchedAccount);
        model.addAttribute("isOwner", searchedAccount.equals(sessionAccount));
        return PROFILE_VIEW_VIEW_NAME;
    }

}