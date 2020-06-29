package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.SessionAccount;
import portfolio2.dto.account.FindPasswordRequestDto;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.AccountService;
import portfolio2.validator.account.FindPasswordRequestDtoValidator;
import portfolio2.validator.account.SignUpRequestDtoValidator;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/account/profile/{userId}")
    public String viewProfile(@PathVariable String userId, @SessionAccount Account sessionAccount, Model model){

        Account accountInDb = profileService.findUser(userId);

        if(accountInDb == null){
            throw new IllegalArgumentException(userId + "에 해당하는 사용자가 없습니다.");
        }

        // 객체 타입의 camel case를 이름으로 준다.
        // mode.addAttribute("account", byUserId)와 같음.
        model.addAttribute("accountInDb", accountInDb);
        model.addAttribute("isOwner", accountInDb.equals(sessionAccount));
        return "account/profile";
    }
    }
}