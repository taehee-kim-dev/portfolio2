package portfolio2.controller.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.config.SessionAccount;
import portfolio2.dto.account.profile.update.ProfileUpdateRequestDto;
import portfolio2.service.ProfileService;
import portfolio2.validator.account.profile.update.ProfileUpdateRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.config.UrlAndViewName.*;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileUpdateRequestDtoValidator profileUpdateRequestDtoValidator;
    private final ModelMapper modelMapper;
    private final ProfileService profileService;

    @InitBinder("profileUpdateRequestDto")
    public void initBinderForProfileUpdateRequestDto(WebDataBinder webDataBinder){
        webDataBinder.addValidators(profileUpdateRequestDtoValidator);
    }

    @GetMapping(SHOW_PROFILE_URL + "/{userId}")
    public String viewProfile(@PathVariable String userId,
                              @SessionAccount Account sessionAccount,
                              Model model){

        Account searchedAccount = profileService.findUser(userId);

        model.addAttribute(SESSION_ACCOUNT, sessionAccount);

        // 존재하지 않는 사용자인 경우
        if(searchedAccount == null){
            model.addAttribute("notFoundError", "존재하지 않는 사용자 입니다.");
            return SHOW_PROFILE_NOT_FOUND_ERORR_VIEW_NAME;
        }

        // 존재하는 사용자인 경우

        // 객체 타입의 camel case를 이름으로 준다.
        // mode.addAttribute("account", byUserId)와 같음.
        model.addAttribute("searchedAccount", searchedAccount);
        model.addAttribute("isOwner", searchedAccount.equals(sessionAccount));
        return SHOW_PROFILE_VIEW_NAME;
    }

    @GetMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String showProfileUpdatePage(@SessionAccount Account sessionAccount, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(modelMapper.map(sessionAccount, ProfileUpdateRequestDto.class));
        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
        return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_SETTING_PROFILE_URL)
    public String updateProfile(@SessionAccount Account sessionAccount,
                                    @Valid @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(profileUpdateRequestDto);
            return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
        }

        profileService.updateProfile(sessionAccount, profileUpdateRequestDto);
        // 한번 쓰고 사라지는 메시지
        // 모델에 포함돼서 전달됨
        redirectAttributes.addFlashAttribute("message", "프로필 수정이 완료되었습니다.");
        return "redirect:" + ACCOUNT_SETTING_PROFILE_URL;
    }
}