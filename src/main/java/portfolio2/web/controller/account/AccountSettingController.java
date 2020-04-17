package portfolio2.web.controller.account;

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
import portfolio2.web.dto.ProfileDto;
import portfolio2.web.dto.ProfileUpdateRequestDto;
import portfolio2.web.validator.ProfileUpdateRequestDtoValidator;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class AccountSettingController {

    public static final String ACCOUNT_SETTING_PROFILE_URL = "/account/setting/profile";
    public static final String ACCOUNT_SETTING_PROFILE_VIEW_NAME = "account/setting/profile";

    private final ProfileUpdateRequestDtoValidator profileUpdateRequestDtoValidator;
    private final AccountService accountService;

    @InitBinder("profileUpdateRequestDto")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(profileUpdateRequestDtoValidator);
    }

    @GetMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){

        model.addAttribute(account);
        model.addAttribute(new ProfileUpdateRequestDto(account));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account,
                                @Valid @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto,
                                Errors errors, Model model,
                                RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(profileUpdateRequestDto);

            return "account/setting/profile";
        }

        accountService.updateProfile(account, profileUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        attributes.addFlashAttribute("message", "프로필 수정이 완료되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_PROFILE_URL;
    }
}
