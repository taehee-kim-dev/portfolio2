//package portfolio2.controller.account;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.InitBinder;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.config.SessionAccount;
//import portfolio2.dto.account.FindPasswordRequestDto;
//import portfolio2.validator.account.FindPasswordRequestDtoValidator;
//
//import javax.validation.Valid;
//import java.util.HashMap;
//
//@Controller
//@RequiredArgsConstructor
//public class FindPasswordController {
//
//    public final String FIND_PASSWORD_URL = "/find-password";
//    public final String FIND_PASSWORD_VIEW_NAME = "account/find-password";
//
//    private final FindPasswordRequestDtoValidator findPasswordRequestDtoValidator;
//    private final FindPasswordService findPasswordService;
//
//    @InitBinder("FindPasswordRequestDto")
//    public void initBinderForFindPasswordRequestDtoValidator(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(findPasswordRequestDtoValidator);
//    }
//
//    // 로그인 되어있으면 안됨.
//    @GetMapping(FIND_PASSWORD_URL)
//    public String showFindPasswordPage(@SessionAccount Account sessionAccount, Model model){
//
//        if(sessionAccount != null){
//            return "redirect:/";
//        }
//
//        model.addAttribute(new FindPasswordRequestDto());
//
//        return FIND_PASSWORD_VIEW_NAME;
//    }
//
//    // 로그인 되어있으면 안됨.
//    @PostMapping(FIND_PASSWORD_URL)
//    public String sendFindPasswordEmail(@SessionAccount Account sessionAccount,
//                                        @Valid @ModelAttribute FindPasswordRequestDto findPasswordRequestDto,
//                                        Errors errors, Model model) {
//
//        if(sessionAccount != null){
//            return "redirect:/";
//        }
//
//        if(errors.hasErrors()){
//            model.addAttribute(findPasswordRequestDto);
//            return FIND_PASSWORD_VIEW_NAME;
//        }
//
//        HashMap<String, String> resultHashMap = findPasswordService.checkFindPasswordEmail(findPasswordRequestDto);
//
//        if(resultHashMap.get("result").equals("notExistingEmail")){
//            model.addAttribute("notExistingEmailError", "가입되지 않은 이메일 입니다.");
//            return FIND_PASSWORD_VIEW_NAME;
//        }else if(resultHashMap.get("result").equals("cannotSendEmail")){
//            model.addAttribute("cannotSendEmailError", "로그인 링크 이메일은 12시간동안 3번만 보낼 수 있습니다.");
//            return FIND_PASSWORD_VIEW_NAME;
//        }
//
//        findPasswordService.sendFindPasswordEmail(findPasswordRequestDto);
//
//        model.addAttribute("successMessage", "비밀번호 찾기 이메일을 발송했습니다.");
//        model.addAttribute(findPasswordRequestDto);
//        return FIND_PASSWORD_VIEW_NAME;
//    }
//
//    // 로그인 되어있는 상태면, 현재 인증 링크에 해당하는 계정으로 재로그인
//    @GetMapping("/check-find-password-link")
//    public String checkFindPasswordLink(@SessionAccount Account sessionAccount,
//                                         String email, String token, Model model) {
//
//        boolean isValid = findPasswordService.isValidLink(email, token);
//
//        if(!isValid){
//            model.addAttribute("error", "유효하지 않은 링크 입니다.");
//            return "account/invalid-find-password-link-error";
//        }
//
//        findPasswordService.logIn(email);
//
//        return "redirect:/account/setting/password";
//    }
//}