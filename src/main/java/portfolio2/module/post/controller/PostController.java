package portfolio2.module.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.post.Post;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.post.validator.PostNewPostRequestDtoValidator;
import portfolio2.module.post.validator.PostUpdateRequestDtoValidator;
import portfolio2.module.tag.Tag;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.ERROR_VIEW_NAME;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.REDIRECT;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.*;

@RequiredArgsConstructor
@Controller
public class PostController {
    private final PostService postService;
    private final PostNewPostRequestDtoValidator postNewPostRequestDtoValidator;
    private final PostUpdateRequestDtoValidator postUpdateRequestDtoValidator;

    @InitBinder("postNewPostRequestDto")
    public void initBinderForPostNewPostRequestDto(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(postNewPostRequestDtoValidator);
    }

    @InitBinder("postUpdateRequestDto")
    public void initBinderForPostUpdateRequestDto(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(postUpdateRequestDtoValidator);
    }

    @GetMapping(POST_VIEW_URL + "/{postId}")
    public String showPost(@SessionAccount Account sessionAccount,
                           @PathVariable("postId") Post post,
                           Model model) {
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        if(post == null){
            model.addAttribute("errorTitle", "게시물 조회 에러");
            model.addAttribute("errorContent", "존재하지 않는 게시물 입니다.");
            return ERROR_VIEW_NAME;
        }
        model.addAttribute(post);
        if(sessionAccount == null){
            model.addAttribute("isAuthor", false);
        }else{
            model.addAttribute("isAuthor", sessionAccount.getUserId().equals(post.getAuthor().getUserId()));
        }
        model.addAttribute("firstWrittenTime", post.getFirstWrittenTime());
        model.addAttribute("tagOnPost", post.getTag().stream().map(Tag::getTitle).collect(Collectors.toList()));
        return POST_VIEW_NAME;
    }

    @GetMapping(POST_NEW_POST_URL)
    public String showPostNewPostForm(@SessionAccount Account sessionAccount, Model model) {
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(new PostNewPostRequestDto());
        return POST_NEW_POST_FORM_VIEW_NAME;
    }

    @PostMapping(POST_NEW_POST_URL)
    public String postNewPost(@SessionAccount Account sessionAccount,
                                  @Valid @ModelAttribute PostNewPostRequestDto postNewPostRequestDto,
                                  Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(postNewPostRequestDto);
            return POST_NEW_POST_FORM_VIEW_NAME;
        }
        Post savedNewPostInDb = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
        postService.sendWebAndEmailNotification(savedNewPostInDb);
        return REDIRECT + POST_VIEW_URL + '/' + savedNewPostInDb.getId();
    }

    @GetMapping(POST_UPDATE_URL + "/{postId}")
    public String showPostUpdateForm(@SessionAccount Account sessionAccount,
                                     @PathVariable("postId") Post post,
                                     Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        if(post == null){
            model.addAttribute("errorTitle", "게시물 조회 에러");
            model.addAttribute("errorContent", "존재하지 않는 게시물 입니다.");
            return ERROR_VIEW_NAME;
        }
        if(!post.getAuthor().getUserId().equals(sessionAccount.getUserId())){
            // post 작성자가 아니면,
            model.addAttribute("errorTitle", "글 수정 권한 없음");
            model.addAttribute("errorContent", "현재 로그인 되어있는 계정이 수정하고자 하는 글의 작성자 계정이 아닙니다.");
            return ERROR_VIEW_NAME;
        }
        String tagTitleOnPost = post.getTag().stream()
                .map(Tag::getTitle)
                .collect(Collectors.joining(","));
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder()
                .postIdToUpdate(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagTitleOnPost(tagTitleOnPost)
                .build();
        model.addAttribute(postUpdateRequestDto);
        return POST_UPDATE_FORM_VIEW_NAME;
    }

    @PostMapping(POST_UPDATE_URL)
    public String updatePost(@SessionAccount Account sessionAccount,
                             @Valid @ModelAttribute PostUpdateRequestDto postUpdateRequestDto,
                             Errors errors, Model model){
        if (errors.hasErrors()) {
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(postUpdateRequestDto);
            return POST_UPDATE_FORM_VIEW_NAME;
        }
        model.addAttribute("errorTitle", "에러");
        model.addAttribute("errorContent", "에러");
        return ERROR_VIEW_NAME;
    }
}
