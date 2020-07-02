//package portfolio2.controller.account;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.config.SessionAccount;
//import portfolio2.dto.account.TagUpdateRequestDto;
//import portfolio2.dto.account.profileupdate.*;
//import portfolio2.validator.account.profileupdate.AccountEmailUpdateRequestDtoValidator;
//import portfolio2.validator.account.profileupdate.AccountNicknameUpdateRequestDtoValidator;
//import portfolio2.validator.account.profileupdate.PasswordUpdateRequestDtoValidator;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Controller
//public class AccountSettingController {
//
//    public static final String ACCOUNT_SETTING_NOTIFICATION_URL = "/account/setting/notification";
//    public static final String ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME = "account/setting/notification";
//
//    public static final String ACCOUNT_SETTING_TAG_URL = "/account/setting/tag";
//    public static final String ACCOUNT_SETTING_TAG_VIEW_NAME = "account/setting/tag";
//
//    public static final String ACCOUNT_SETTING_PASSWORD_URL = "/account/setting/password";
//    public static final String ACCOUNT_SETTING_PASSWORD_VIEW_NAME = "account/setting/password";
//
//    public static final String ACCOUNT_SETTING_ACCOUNT_URL = "/account/setting/account";
//    public static final String ACCOUNT_SETTING_ACCOUNT_VIEW_NAME = "account/setting/account";
//
//    public static final String ACCOUNT_SETTING_ACCOUNT_EMAIL_URL = "/account/setting/account/email";
//    public static final String ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL = "/account/setting/account/nickname";
//
//    private final PasswordUpdateRequestDtoValidator passwordUpdateRequestDtoValidator;
//    private final AccountNicknameUpdateRequestDtoValidator accountNicknameUpdateRequestDtoValidator;
//    private final AccountEmailUpdateRequestDtoValidator accountEmailUpdateRequestDtoValidator;
//
//    private final AccountSettingService accountSettingService;
//
//    private final ModelMapper modelMapper;
//    private final ObjectMapper objectMapper;
//
//    @InitBinder("passwordUpdateRequestDto")
//    public void initBinderForPasswordUpdateRequestDto(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(passwordUpdateRequestDtoValidator);
//    }
//
//    @InitBinder("accountNicknameUpdateRequestDto")
//    public void initBinderForAccountNicknameUpdateRequestDto(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(accountNicknameUpdateRequestDtoValidator);
//    }
//
//    @InitBinder("accountEmailUpdateRequestDto")
//    public void initBinderForAccountEmailUpdateRequestDto(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(accountEmailUpdateRequestDtoValidator);
//    }
//
//
//    // 알림 설정
//
//    @GetMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
//    public String showNotificationUpdateView(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute("sessionAccount", sessionAccount);
//        model.addAttribute(modelMapper.map(sessionAccount, NotificationUpdateRequestDto.class));
//        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
//        return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
//    }
//
//    @PostMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
//    public String notificationUpdate(@SessionAccount Account sessionAccount,
//                                     @Valid @ModelAttribute NotificationUpdateRequestDto notificationUpdateRequestDto,
//                                     Errors errors, Model model,
//                                     RedirectAttributes redirectAttributes){
//        if(errors.hasErrors()){
//            model.addAttribute("sessionAccount", sessionAccount);
//            model.addAttribute(notificationUpdateRequestDto);
//            return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
//        }
//
//        accountSettingService.updateNotification(sessionAccount, notificationUpdateRequestDto);
//        // 한번 쓰고 사라지는 메시지
//        // 모델에 포함돼서 전달됨
//        redirectAttributes.addFlashAttribute("message", "알림설정이 저장되었습니다.");
//        return "redirect:" + ACCOUNT_SETTING_NOTIFICATION_URL;
//    }
//
//
//    // 관심 태그 설정
//
//    @GetMapping(ACCOUNT_SETTING_TAG_URL)
//    public String showTagUpdateView(@SessionAccount Account sessionAccount, Model model) throws JsonProcessingException {
//        model.addAttribute("sessionAccount", sessionAccount);
//        List<String> tag = accountSettingService.getTagOfAccount(sessionAccount);
//        model.addAttribute("tag", tag);
//        return ACCOUNT_SETTING_TAG_VIEW_NAME;
//    }
//
//    @ResponseBody
//    @PostMapping(ACCOUNT_SETTING_TAG_URL + "/add")
//    public ResponseEntity<String> addTag(@SessionAccount Account sessionAccount,
//                                 @RequestBody TagUpdateRequestDto tagUpdateRequestDto){
//        accountSettingService.addTagToAccount(sessionAccount, tagUpdateRequestDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @ResponseBody
//    @PostMapping(ACCOUNT_SETTING_TAG_URL + "/remove")
//    public ResponseEntity<String> removeTag(@SessionAccount Account sessionAccount,
//                                    @RequestBody TagUpdateRequestDto tagUpdateRequestDto){
//
//        boolean result = accountSettingService.removeTagFromAccount(sessionAccount, tagUpdateRequestDto);
//
//        if(!result){
//            return ResponseEntity.badRequest().build();
//        }
//
//        return ResponseEntity.ok().build();
//    }
//
//    // 비밀번호 변경
//
//    @GetMapping(ACCOUNT_SETTING_PASSWORD_URL)
//    public String showPasswordUpdatePage(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute("sessionAccount", sessionAccount);
//        model.addAttribute(new PasswordUpdateRequestDto());
//        return ACCOUNT_SETTING_PASSWORD_VIEW_NAME;
//    }
//
//    @PostMapping(ACCOUNT_SETTING_PASSWORD_URL)
//    public String postPasswordUpdate(@SessionAccount Account sessionAccount,
//                                    @Valid @ModelAttribute PasswordUpdateRequestDto passwordUpdateRequestDto,
//                                    Errors errors, Model model,
//                                    RedirectAttributes redirectAttributes){
//        if(errors.hasErrors()){
//            model.addAttribute("sessionAccount", sessionAccount);
//            model.addAttribute(passwordUpdateRequestDto);
//            return ACCOUNT_SETTING_PASSWORD_VIEW_NAME;
//        }
//
//        accountSettingService.updatePassword(sessionAccount, passwordUpdateRequestDto);
//
//        // 한번 쓰고 사라지는 메시지
//        // 모델에 포함돼서 전달됨
//        redirectAttributes.addFlashAttribute("message", "비밀번호 변경이 완료되었습니다.");
//        return "redirect:" + ACCOUNT_SETTING_PASSWORD_URL;
//    }
//
//    // 계정 정보 변경
//
//    // 닉네임 변경
//
//    @GetMapping(ACCOUNT_SETTING_ACCOUNT_URL)
//    public String showAccountSettingAccountView(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute("sessionAccount", sessionAccount);
//        model.addAttribute(modelMapper.map(sessionAccount, AccountNicknameUpdateRequestDto.class));
//        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
//        return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
//    }
//
//    @PostMapping(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
//    public String updateAccountNickname(@SessionAccount Account sessionAccount,
//                                         @Valid @ModelAttribute AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto,
//                                         Errors errors, Model model,
//                                         RedirectAttributes redirectAttributes){
//        if(errors.hasErrors()){
//            model.addAttribute("sessionAccount", sessionAccount);
//            model.addAttribute(accountNicknameUpdateRequestDto);
//            return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
//        }
//
//        accountSettingService.updateAccountNickname(sessionAccount, accountNicknameUpdateRequestDto);
//        // 한번 쓰고 사라지는 메시지
//        // 모델에 포함돼서 전달됨
//        redirectAttributes.addFlashAttribute("message", "닉네임 변경이 완료되었습니다.");
//        return "redirect:" + ACCOUNT_SETTING_ACCOUNT_URL;
//    }
//
//    // 이메일 변경
//
//    @PostMapping(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
//    public String updateAccountNickname(@SessionAccount Account sessionAccount,
//                                        @Valid @ModelAttribute AccountEmailUpdateRequestDto accountEmailUpdateRequestDto,
//                                        Errors errors, Model model,
//                                        RedirectAttributes redirectAttributes){
//        if(errors.hasErrors()){
//            model.addAttribute("sessionAccount", sessionAccount);
//            model.addAttribute(accountEmailUpdateRequestDto);
//            return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
//        }
//
//        accountSettingService.updateAccountEmail(sessionAccount, accountEmailUpdateRequestDto);
//        // 한번 쓰고 사라지는 메시지
//        // 모델에 포함돼서 전달됨
//        redirectAttributes.addFlashAttribute("message", "인증 이메일을 발송했습니다.");
//        return "redirect:" + ACCOUNT_SETTING_ACCOUNT_URL;
//    }
//}
