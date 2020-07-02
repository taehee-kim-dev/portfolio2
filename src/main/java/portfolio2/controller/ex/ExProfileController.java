package portfolio2.controller.ex;//package portfolio2.controller.account;
//
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.SessionAccount;
//import portfolio2.dto.account.profileupdate.ProfileUpdateRequestDto;
//import portfolio2.validator.account.profileupdate.ProfileUpdateRequestDtoValidator;
//
//import javax.validation.Valid;
//
//@Controller
//@RequiredArgsConstructor
//public class ProfileController {
//
//    public static final String ACCOUNT_SETTING_PROFILE_URL = "/account/setting/profile";
//    public static final String ACCOUNT_SETTING_PROFILE_VIEW_NAME = "account/setting/profile";
//
//    private final ProfileUpdateRequestDtoValidator profileUpdateRequestDtoValidator;
//    private final ModelMapper modelMapper;
//    private final ProfileService profileService;
//
//    @InitBinder("profileUpdateRequestDto")
//    public void initBinderForProfileUpdateRequestDto(WebDataBinder webDataBinder){
//        webDataBinder.addValidators(profileUpdateRequestDtoValidator);
//    }
//
//    @GetMapping("/account/profile/{userId}")
//    public String viewProfile(@PathVariable String userId, @SessionAccount Account sessionAccount, Model model){
//
//        Account accountInDb = profileService.findUser(userId);
//
//        if(accountInDb == null){
//            throw new IllegalArgumentException(userId + "에 해당하는 사용자가 없습니다.");
//        }
//
//        // 객체 타입의 camel case를 이름으로 준다.
//        // mode.addAttribute("account", byUserId)와 같음.
//        model.addAttribute("sessionAccount", sessionAccount);
//        model.addAttribute("accountInDb", accountInDb);
//        model.addAttribute("isOwner", accountInDb.equals(sessionAccount));
//        return "account/profile";
//    }
//
//    @GetMapping(ACCOUNT_SETTING_PROFILE_URL)
//    public String getProfileUpdate(@SessionAccount Account sessionAccount, Model model){
//        model.addAttribute("sessionAccount", sessionAccount);
//        model.addAttribute(modelMapper.map(sessionAccount, ProfileUpdateRequestDto.class));
//        // 아래 문장 생략하면 GetMapping url로 view name 간주함.
//        return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
//    }
//
//    @PostMapping(ACCOUNT_SETTING_PROFILE_URL)
//    public String updateProfile(@SessionAccount Account sessionAccount,
//                                    @Valid @ModelAttribute ProfileUpdateRequestDto profileUpdateRequestDto,
//                                    Errors errors, Model model,
//                                    RedirectAttributes redirectAttributes){
//        if(errors.hasErrors()){
//            model.addAttribute("sessionAccount", sessionAccount);
//            model.addAttribute(profileUpdateRequestDto);
//            return ACCOUNT_SETTING_PROFILE_VIEW_NAME;
//        }
//
//        profileService.updateProfile(sessionAccount, profileUpdateRequestDto);
//        // 한번 쓰고 사라지는 메시지
//        // 모델에 포함돼서 전달됨
//        redirectAttributes.addFlashAttribute("message", "프로필 수정이 완료되었습니다.");
//        return "redirect:" + ACCOUNT_SETTING_PROFILE_URL;
//    }
//}