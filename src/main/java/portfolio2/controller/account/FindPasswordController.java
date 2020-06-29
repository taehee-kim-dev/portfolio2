package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
public class FindPasswordController {

    public final String FIND_PASSWORD_URL = "/find-password";
    public final String FIND_PASSWORD_VIEW_NAME = "account/find-password";

    private final FindPasswordRequestDtoValidator findPasswordRequestDtoValidator;
    private final AccountService accountService;
    private final EmailVerificationService emailVerificationService;
    private final AccountRepository accountRepository;

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
    
    @GetMapping(FIND_PASSWORD_URL)
    public String showFindPasswordPage(Model model){

        model.addAttribute(new FindPasswordRequestDto());

        return FIND_PASSWORD_VIEW_NAME;
    }

    @PostMapping(FIND_PASSWORD_URL)
    public String sendEmailLoginLink(
            @Valid @ModelAttribute FindPasswordRequestDto findPasswordRequestDto,
            Errors errors, Model model) {

        if(errors.hasErrors()){
            model.addAttribute(findPasswordRequestDto);

            return "find-password";
        }

        Account accountInDb = accountRepository.findByEmail(findPasswordRequestDto.getEmail());

        if (accountInDb == null) {
            model.addAttribute("notExistingEmailError", "가입되지 않은 이메일 입니다.");
            return "find-password";
        }

        if (!accountInDb.canSendLoginEmail()) {
            model.addAttribute("emailCannotSendError", "로그인 링크 이메일은 12시간동안 3번만 보낼 수 있습니다.");
            return "find-password";
        }

        accountService.sendLoginEmail(accountInDb);
        model.addAttribute("successMessage", "로그인 링크를 이메일로 발송했습니다.");
        model.addAttribute(findPasswordRequestDto);
        return "find-password";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {

        Account accountInDb = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";

        if (accountInDb == null || !accountInDb.isValidTokenForEmailLogin(token)) {
            model.addAttribute("error", "잘못된 링크입니다.");
            return view;
        }

        accountService.loginOrUpdateSessionAccount(accountInDb);
        return view;
    }
}