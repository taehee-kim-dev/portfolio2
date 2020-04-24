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
import portfolio2.domain.account.CurrentUser;
import portfolio2.service.AccountService;
import portfolio2.dto.NotificationUpdateDto;
import portfolio2.dto.PasswordUpdateRequestDto;
import portfolio2.dto.ProfileUpdateRequestDto;
import portfolio2.validator.PasswordUpdateRequestDtoValidator;
import portfolio2.validator.ProfileUpdateRequestDtoValidator;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class AccountSettingController {



    public static final String ACCOUNT_SETTING_PROFILE_URL = "/account/setting/profile";
    public static final String ACCOUNT_SETTING_PROFILE_VIEW_NAME = "account/setting/profile";

    public static final String ACCOUNT_SETTING_PASSWORD_URL = "/account/setting/password";
    public static final String ACCOUNT_SETTING_PASSWORD_VIEW_NAME = "account/setting/password";

    public static final String ACCOUNT_SETTING_NOTIFICATION_URL = "/account/setting/notification";
    public static final String ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME = "account/setting/notification";



    private final ProfileUpdateRequestDtoValidator profileUpdateRequestDtoValidator;
    private final PasswordUpdateRequestDtoValidator passwordUpdateRequestDtoValidator;



    private final AccountService accountService;



    @InitBinder("profileUpdateRequestDto")
    public void initBinderForProfileUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(profileUpdateRequestDtoValidator);
    }

    @InitBinder("passwordUpdateRequestDto")
    public void initBinderForPasswordUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(passwordUpdateRequestDtoValidator);
    }



    @GetMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String getProfileUpdate(@CurrentUser Account sessionAccount, Model model){
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(new ProfileUpdateRequestDto(sessionAccount));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String postProfileUpdate(@CurrentUser Account sessionAccount,
                                @Valid @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto,
                                Errors errors, Model model,
                                RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("sessionAccount", sessionAccount);
            model.addAttribute(profileUpdateRequestDto);

            return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(sessionAccount, profileUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "프로필 수정이 완료되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_PROFILE_URL;
    }



    @GetMapping(ACCOUNT_SETTING_PASSWORD_URL)
    public String getPasswordUpdate(@CurrentUser Account sessionAccount, Model model){
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(new PasswordUpdateRequestDto());

        return ACCOUNT_SETTING_PASSWORD_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_PASSWORD_URL)
    public String postPasswordUpdate(@CurrentUser Account sessionAccount,
                                    @Valid @ModelAttribute PasswordUpdateRequestDto passwordUpdateRequestDto,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("sessionAccount", sessionAccount);
            model.addAttribute(passwordUpdateRequestDto);

            return ACCOUNT_SETTING_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(sessionAccount, passwordUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "비밀번호 변경이 완료되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_PASSWORD_URL;
    }


    @GetMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
    public String getNotificationUpdate(@CurrentUser Account sessionAccount, Model model){
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(new NotificationUpdateDto(sessionAccount));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
    public String postNotificationUpdate(@CurrentUser Account sessionAccount,
                                    @Valid @ModelAttribute NotificationUpdateDto notificationUpdateDto,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("sessionAccount", sessionAccount);
            model.addAttribute(notificationUpdateDto);

            return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
        }

        //accountService.updateNotification(sessionAccount, notificationUpdateDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "알림설정이 저장되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_NOTIFICATION_URL;
    }
}
