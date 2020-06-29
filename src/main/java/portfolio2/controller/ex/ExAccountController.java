//package portfolio2.controller.ex;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.AccountRepository;
//import portfolio2.domain.account.SessionAccount;
//import portfolio2.dto.account.FindPasswordRequestDto;
//import portfolio2.service.account.AccountService;
//import portfolio2.dto.account.SignUpRequestDto;
//import portfolio2.validator.account.FindPasswordRequestDtoValidator;
//import portfolio2.validator.account.SignUpRequestDtoValidator;
//
//import javax.validation.Valid;
//
//@Controller
//@RequiredArgsConstructor
//public class AccountController {
//
//    private final SignUpRequestDtoValidator signUpRequestDtoValidator;
//    private final FindPasswordRequestDtoValidator findPasswordRequestDtoValidator;
//    private final AccountService accountService;
//    private final AccountRepository accountRepository;
//
//    private void setSessionAccount(@SessionAccount Account sessionAccount, Model model) {
//        if (sessionAccount != null) {
//            model.addAttribute("sessionAccount", sessionAccount);
//        }
//    }
//
//    @InitBinder("signUpRequestDto")
//    public void initBinderForSignUpRequestDtoValidator(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(signUpRequestDtoValidator);
//    }
//
//    @InitBinder("sendEmailLoginLinkRequestDto")
//    public void initBinderForSendEmailLoginLinkRequestDtoValidator(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(findPasswordRequestDtoValidator);
//    }
//
//    @GetMapping("/sign-up")
//    public String showSignUpPage(Model model) {
//        // name값을 생략하면, 객체 이름의 camel 케이스를 이름으로 줌.
//        // model.addAttribute("signUpRequestDto", new SignUpRequestDto()); 와 동일.
//        model.addAttribute(new SignUpRequestDto());
//        return "account/sign-up";
//    }
//
//    /*
//        복합 객체(여러 필드를 갖고 있는 객체)를 받아올 때는
//        원래 @ModelAttribute를 써서 받아와야 한다.
//        @Valid @ModelAttribute SignUpRequestDto signupForm
//        생략 가능.
//    * */
//    @PostMapping("/sign-up")
//    public String signUp(@Valid @ModelAttribute SignUpRequestDto signUpRequestDto, Errors errors) {
//        if (errors.hasErrors()) {
//            // Validation에서 error가 발생하면,
//            // form을 다시 보여준다.
//            return "account/sign-up";
//        }
//
//        accountService.processNewAccount(signUpRequestDto);
//
//        // 홈 페이지로 리다이렉트
//        return "redirect:/";
//    }
//
//    @GetMapping("/check-email-token")
//    public String checkEmailToken(String token, String email, Model model){
//
//        Account accountInDb = accountRepository.findByEmail(email);
//        String view = "account/checked-email";
//        if(accountInDb == null){
//            model.addAttribute("error", "invalidEmail");
//            return view;
//        }
//
//        if(!accountInDb.isValidTokenForEmailCheck(token)){
//            model.addAttribute("error", "invalidToken");
//            return view;
//        }
//
//        accountService.completeSignUp(accountInDb);
//
//        model.addAttribute("nickname", accountInDb.getNickname());
//        model.addAttribute("userId", accountInDb.getUserId());
//        model.addAttribute("email", accountInDb.getEmail());
//
//        return view;
//    }
//
//    @GetMapping("/check-email")
//    public String checkEmail(@SessionAccount Account sessionAccount, Model model) {
//        setSessionAccount(sessionAccount, model);
//        model.addAttribute("email", sessionAccount.getEmail());
//        return "account/check-email";
//    }
//
//    @GetMapping("/resend-confirm-email")
//    public String resendConfirmEmail(@SessionAccount Account sessionAccount, Model model) {
//
//        if (!accountRepository.findByUserId(sessionAccount.getUserId()).canSendConfirmEmail()) {
//            setSessionAccount(sessionAccount, model);
//            model.addAttribute("error", "인증 이메일은 12시간동안 5번만 보낼 수 있습니다.");
//            model.addAttribute("email", sessionAccount.getEmail());
//            return "account/check-email";
//        }
//
//        accountService.resendSignUpConfirmEmail(sessionAccount);
//        return "redirect:/";
//    }
//
//    @GetMapping("/account/profile/{userId}")
//    public String viewProfile(@PathVariable String userId, @SessionAccount Account sessionAccount, Model model){
//        setSessionAccount(sessionAccount, model);
//        Account accountInDb = accountRepository.findByUserId(userId);
//        if(accountInDb == null){
//            throw new IllegalArgumentException(userId + "에 해당하는 사용자가 없습니다.");
//        }
//        // 객체 타입의 camel case를 이름으로 준다.
//        // mode.addAttribute("account", byUserId)와 같음.
//        model.addAttribute("accountInDb", accountInDb);
//        model.addAttribute("isOwner", accountInDb.equals(sessionAccount));
//        return "account/profile";
//    }
//
//    @GetMapping("/email-login")
//    public String getEmailLogin(Model model){
//
//        model.addAttribute(new FindPasswordRequestDto());
//
//        return "find-password";
//    }
//
//    @PostMapping("/email-login")
//    public String sendEmailLoginLink(
//            @Valid @ModelAttribute FindPasswordRequestDto findPasswordRequestDto,
//            Errors errors, Model model) {
//
//        if(errors.hasErrors()){
//            model.addAttribute(findPasswordRequestDto);
//
//            return "find-password";
//        }
//
//        Account accountInDb = accountRepository.findByEmail(findPasswordRequestDto.getEmail());
//
//        if (accountInDb == null) {
//            model.addAttribute("notExistingEmailError", "가입되지 않은 이메일 입니다.");
//            return "find-password";
//        }
//
//        if (!accountInDb.canSendLoginEmail()) {
//            model.addAttribute("emailCannotSendError", "로그인 링크 이메일은 12시간동안 3번만 보낼 수 있습니다.");
//            return "find-password";
//        }
//
//        accountService.sendLoginEmail(accountInDb);
//        model.addAttribute("successMessage", "로그인 링크를 이메일로 발송했습니다.");
//        model.addAttribute(findPasswordRequestDto);
//        return "find-password";
//    }
//
//    @GetMapping("/login-by-email")
//    public String loginByEmail(String token, String email, Model model) {
//
//        Account accountInDb = accountRepository.findByEmail(email);
//        String view = "account/logged-in-by-email";
//
//        if (accountInDb == null || !accountInDb.isValidTokenForEmailLogin(token)) {
//            model.addAttribute("error", "잘못된 링크입니다.");
//            return view;
//        }
//
//        accountService.loginOrUpdateSessionAccount(accountInDb);
//        return view;
//    }
//}