package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.SessionAccount;
import portfolio2.dto.account.FindPasswordRequestDto;
import portfolio2.service.AccountService;
import portfolio2.validator.account.FindPasswordRequestDtoValidator;

import javax.validation.Valid;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @InitBinder("FindPasswordRequestDto")
    public void initBinderForFindPasswordRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(findPasswordRequestDtoValidator);
    }

    @GetMapping("/check-email-verification-link")
    public String checkEmailVerificationLink(String email, String token, Model model){

        String view = "account/email-verification-result";

        HashMap<String, String> resultHashMap = emailVerificationService.checkEmailVerificationLink(email, token);

        if(resultHashMap.get("result").equals("notExistingEmail")){
            model.addAttribute("error", "notExistingEmail");
            return view;
        }else if(resultHashMap.get("result").equals("invalidToken")){
            model.addAttribute("error", "invalidToken");
            return view;
        }

        model.addAttribute("nickname", resultHashMap.get("nickname"));
        model.addAttribute("userId", resultHashMap.get("userId"));
        model.addAttribute("email", resultHashMap.get("email"));

        return view;
    }

    @GetMapping("/send-email-verification-link")
    public String sendEmailVerificationLink(@SessionAccount Account sessionAccount,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {

        if (!emailVerificationService.canSendEmailVerificationLink(Account sessionAccount)) {
            model.addAttribute("error", "인증 이메일은 12시간동안 5번만 보낼 수 있습니다.");
            return "account/email-verification-result";
        }

        redirectAttributes.addFlashAttribute("message", "인증 이메일을 보냈습니다.");
        return "redirect:/account/setting/account";
    }
}