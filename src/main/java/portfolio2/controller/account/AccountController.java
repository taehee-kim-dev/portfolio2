package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.CurrentUser;
import portfolio2.service.AccountService;
import portfolio2.dto.SignUpRequestDto;
import portfolio2.validator.SignUpRequestDtoValidator;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpRequestDtoValidator signUpRequestDtoValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    private void setSessionAccount(@CurrentUser Account sessionAccount, Model model) {
        if (sessionAccount != null) {
            model.addAttribute("sessionAccount", sessionAccount);
        }
    }

    @InitBinder("signUpRequestDto")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpRequestDtoValidator);
    }

    @GetMapping("/sign-up")
    public String signUpGet(Model model) {
        // name값을 생략하면, 객체 이름의 camel 케이스를 이름으로 줌.
        // model.addAttribute("signUpRequestDto", new SignUpRequestDto()); 와 동일.
        model.addAttribute(new SignUpRequestDto());
        return "account/sign-up";
    }

    /*
        복합 객체(여러 필드를 갖고 있는 객체)를 받아올 때는
        원래 @ModelAttribute를 써서 받아와야 한다.
        @Valid @ModelAttribute SignUpRequestDto signupForm
        생략 가능.
    * */
    @PostMapping("/sign-up")
    public String signUpPost(@Valid @ModelAttribute SignUpRequestDto signUpRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            // Validation에서 error가 발생하면,
            // form을 다시 보여준다.
            return "account/sign-up";
        }

        accountService.processNewAccount(signUpRequestDto);

        // 홈 페이지로 리다이렉트
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@CurrentUser Account sessionAccount, String token, String email, Model model){

        setSessionAccount(sessionAccount, model);

        Account accountInDb = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(accountInDb == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if(!accountInDb.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignUp(accountInDb);

        model.addAttribute("nickname", accountInDb.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account sessionAccount, Model model) {
        setSessionAccount(sessionAccount, model);
        model.addAttribute("email", sessionAccount.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account sessionAccount, Model model) {
        setSessionAccount(sessionAccount, model);
        if (!accountRepository.findByUserId(sessionAccount.getUserId()).canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 12시간동안 5번만 보낼 수 있습니다.");
            model.addAttribute("email", sessionAccount.getEmail());
            return "account/check-email";
        }

        accountService.resendSignUpConfirmEmail(sessionAccount);
        return "redirect:/";
    }

    @GetMapping("account/profile/{userId}")
    public String viewProfile(@PathVariable String userId, @CurrentUser Account sessionAccount, Model model){
        setSessionAccount(sessionAccount, model);
        Account accountInDb = accountRepository.findByUserId(userId);
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