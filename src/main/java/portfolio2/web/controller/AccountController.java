package portfolio2.web.controller;

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
import portfolio2.web.dto.SignUpRequestDto;
import portfolio2.web.validator.SignUpRequestDtoValidator;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpRequestDtoValidator signUpRequestDtoValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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

        Account account = accountService.processNewAccount(signUpRequestDto);
        accountService.login(account);

        // 홈 페이지로 리다이렉트
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignUp(account);

        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        if (!accountRepository.findByUserId(account.getUserId()).canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 12시간동안 5번만 보낼 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.resendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{userId}")
    public String viewProfile(@PathVariable String userId, @CurrentUser Account account, Model model){
        Account byUserId = accountRepository.findByUserId(userId);
        if(userId == null){
            throw new IllegalArgumentException(userId + "에 해당하는 사용자가 없습니다.");
        }
        // 객체 타입의 camel case를 이름으로 준다.
        // mode.addAttribute("account", byUserId)와 같음.
        model.addAttribute(byUserId);
        model.addAttribute("isOwner", byUserId.equals(account));
        return "account/profile";
    }
}