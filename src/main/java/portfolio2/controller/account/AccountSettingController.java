package portfolio2.controller.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.CurrentUser;
import portfolio2.dto.account.TagUpdateRequestDto;
import portfolio2.dto.account.profileupdate.*;
import portfolio2.service.AccountService;
import portfolio2.domain.tag.TagRepository;
import portfolio2.validator.account.profileupdate.AccountNicknameUpdateRequestDtoValidator;
import portfolio2.validator.account.profileupdate.PasswordUpdateRequestDtoValidator;
import portfolio2.validator.account.profileupdate.ProfileUpdateRequestDtoValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class AccountSettingController {

    public static final String ACCOUNT_SETTING_PROFILE_URL = "/account/setting/profile";
    public static final String ACCOUNT_SETTING_PROFILE_VIEW_NAME = "account/setting/profile";

    public static final String ACCOUNT_SETTING_PASSWORD_URL = "/account/setting/password";
    public static final String ACCOUNT_SETTING_PASSWORD_VIEW_NAME = "account/setting/password";

    public static final String ACCOUNT_SETTING_NOTIFICATION_URL = "/account/setting/notification";
    public static final String ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME = "account/setting/notification";

    public static final String ACCOUNT_SETTING_ACCOUNT_URL = "/account/setting/account";
    public static final String ACCOUNT_SETTING_ACCOUNT_VIEW_NAME = "account/setting/account";

    public static final String ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL = "/account/setting/account/nickname";

    public static final String ACCOUNT_SETTING_TAG_URL = "/account/setting/tag";
    public static final String ACCOUNT_SETTING_TAG_VIEW_NAME = "account/setting/tag";


    private final ProfileUpdateRequestDtoValidator profileUpdateRequestDtoValidator;
    private final PasswordUpdateRequestDtoValidator passwordUpdateRequestDtoValidator;
    private final AccountNicknameUpdateRequestDtoValidator accountNicknameUpdateRequestDtoValidator;

    private final TagRepository tagRepository;

    private final AccountService accountService;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;



    @InitBinder("profileUpdateRequestDto")
    public void initBinderForProfileUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(profileUpdateRequestDtoValidator);
    }

    @InitBinder("passwordUpdateRequestDto")
    public void initBinderForPasswordUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(passwordUpdateRequestDtoValidator);
    }

    @InitBinder("accountNicknameUpdateRequestDto")
    public void initBinderForAccountNicknameUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(accountNicknameUpdateRequestDtoValidator);
    }

    @GetMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String getProfileUpdate(@CurrentUser Account sessionAccount, Model model){
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, ProfileUpdateRequestDto.class));
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
    public String showNotificationUpdateView(@CurrentUser Account sessionAccount, Model model){
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, NotificationUpdateRequestDto.class));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_NOTIFICATION_URL)
    public String notificationUpdate(@CurrentUser Account sessionAccount,
                                    @Valid @ModelAttribute NotificationUpdateRequestDto notificationUpdateRequestDto,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("sessionAccount", sessionAccount);
            model.addAttribute(notificationUpdateRequestDto);

            return ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME;
        }

        accountService.updateNotification(sessionAccount, notificationUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "알림설정이 저장되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_NOTIFICATION_URL;
    }

    @GetMapping(ACCOUNT_SETTING_ACCOUNT_URL)
    public String showAccountSettingAccountView(@CurrentUser Account sessionAccount, Model model){
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, AccountNicknameUpdateRequestDto.class));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
    public String updateAccountNickname(@CurrentUser Account sessionAccount,
                                         @Valid @ModelAttribute AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto,
                                         Errors errors, Model model,
                                         RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("sessionAccount", sessionAccount);
            model.addAttribute(accountNicknameUpdateRequestDto);

            return ACCOUNT_SETTING_ACCOUNT_VIEW_NAME;
        }

        accountService.updateAccountNickname(sessionAccount, accountNicknameUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "닉네임 변경이 완료되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_ACCOUNT_URL;
    }

    @GetMapping(ACCOUNT_SETTING_TAG_URL)
    public String showTagUpdateView(@CurrentUser Account sessionAccount, Model model) throws JsonProcessingException {
        model.addAttribute("sessionAccount", sessionAccount);
        List<String> tag = accountService.getTag(sessionAccount);
        model.addAttribute("tag", tag);

        List<String> allExistingTag = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allExistingTag));

        return ACCOUNT_SETTING_TAG_VIEW_NAME;
    }

    @ResponseBody
    @PostMapping(ACCOUNT_SETTING_TAG_URL + "/add")
    public ResponseEntity addTag(@CurrentUser Account sessionAccount,
                                 @RequestBody TagUpdateRequestDto tagUpdateRequestDto){

        String newTagTitle = tagUpdateRequestDto.getTagTitle();

        Tag existingTagInDb = tagRepository.findByTitle(newTagTitle);

        if(existingTagInDb == null){
            existingTagInDb = tagRepository.save(Tag.builder().title(newTagTitle).build());
        }

        accountService.addTag(sessionAccount, existingTagInDb);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping(ACCOUNT_SETTING_TAG_URL + "/remove")
    public ResponseEntity removeTag(@CurrentUser Account sessionAccount,
                                    @RequestBody TagUpdateRequestDto tagUpdateRequestDto){

        String tagTitleToRemove = tagUpdateRequestDto.getTagTitle();

        Tag existingTagInDb = tagRepository.findByTitle(tagTitleToRemove);

        if(existingTagInDb == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(sessionAccount, existingTagInDb);

        return ResponseEntity.ok().build();
    }
}
