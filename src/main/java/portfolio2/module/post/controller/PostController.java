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
import portfolio2.module.post.dto.PostDeleteRequestDto;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.post.validator.PostNewPostRequestDtoValidator;
import portfolio2.module.post.validator.PostUpdateRequestDtoValidator;
import portfolio2.module.tag.Tag;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static portfolio2.module.main.config.StaticVariableNamesAboutMain.*;
import static portfolio2.module.post.controller.config.StaticVariableNamesAboutPost.*;

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
            model.addAttribute(ERROR_TITLE, "게시물 조회 에러");
            model.addAttribute(ERROR_CONTENT, "존재하지 않는 게시물 입니다.");
            return ERROR_VIEW_NAME;
        }
        model.addAttribute(post);
        model.addAttribute(new PostDeleteRequestDto(post.getId()));
        if(sessionAccount == null){
            model.addAttribute("isAuthor", false);
        }else{
            model.addAttribute("isAuthor", sessionAccount.getUserId().equals(post.getAuthor().getUserId()));
        }
        model.addAttribute("firstWrittenTime", post.getFirstWrittenDateTime());
        model.addAttribute("tagOnPost", post.getCurrentTag().stream().map(Tag::getTitle).collect(Collectors.toList()));
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
            if (errors.hasFieldErrors("tagTitleOnPost")){
                model.addAttribute(ERROR_TITLE, "요청 에러");
                model.addAttribute(ERROR_CONTENT, "잘못된 접근입니다.");
                return ERROR_VIEW_NAME;
            }
            model.addAttribute(postNewPostRequestDto);
            return POST_NEW_POST_FORM_VIEW_NAME;
        }
        Post savedNewPostInDb = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
        postService.sendWebAndEmailNotificationOfNewPost(savedNewPostInDb);
        return REDIRECT + POST_VIEW_URL + '/' + savedNewPostInDb.getId();
    }

    @GetMapping(POST_UPDATE_URL + "/{postId}")
    public String showPostUpdateForm(@SessionAccount Account sessionAccount,
                                     @PathVariable("postId") Post post, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        if(post == null){
            model.addAttribute(ERROR_TITLE, "게시물 조회 에러");
            model.addAttribute(ERROR_CONTENT, "존재하지 않는 게시물 입니다.");
            return ERROR_VIEW_NAME;
        }
        if(!post.getAuthor().getUserId().equals(sessionAccount.getUserId())){
            // post 작성자가 아니면,
            model.addAttribute(ERROR_TITLE, "글 수정 권한 없음");
            model.addAttribute(ERROR_CONTENT, "현재 로그인 되어있는 계정이 수정하고자 하는 글의 작성자 계정이 아닙니다.");
            return ERROR_VIEW_NAME;
        }
        String tagTitleOnPost = post.getCurrentTag().stream()
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
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        PostErrorType postErrorType =  postService.postUpdateErrorCheck(sessionAccount, postUpdateRequestDto);
        if(postErrorType == PostErrorType.POST_NOT_FOUND){
            model.addAttribute(ERROR_TITLE, "게시물 수정 에러");
            model.addAttribute(ERROR_CONTENT, "존재하지 않는 게시물 입니다.");
            return ERROR_VIEW_NAME;
        }
        if(postErrorType == PostErrorType.NOT_AUTHOR){
            // post 작성자가 아니면,
            model.addAttribute(ERROR_TITLE, "글 수정 권한 없음");
            model.addAttribute(ERROR_CONTENT, "현재 로그인 되어있는 계정이 수정하고자 하는 글의 작성자 계정이 아닙니다.");
            return ERROR_VIEW_NAME;
        }
        if (errors.hasErrors()) {
            if (errors.hasFieldErrors("tagTitleOnPost")){
                model.addAttribute(ERROR_TITLE, "요청 에러");
                model.addAttribute(ERROR_CONTENT, "잘못된 접근입니다.");
                return ERROR_VIEW_NAME;
            }
            model.addAttribute(postUpdateRequestDto);
            return POST_UPDATE_FORM_VIEW_NAME;
        }
        Post updatedPostInDb = postService.updatePost(postUpdateRequestDto);
        postService.sendWebAndEmailNotificationOfUpdatedPost(updatedPostInDb);
        return REDIRECT + POST_VIEW_URL + '/' + updatedPostInDb.getId();
    }

    @PostMapping(POST_DELETE_URL)
    public String deletePost(@SessionAccount Account sessionAccount,
                             @Valid @ModelAttribute PostDeleteRequestDto postDeleteRequestDto, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        PostErrorType postErrorType =  postService.postDeleteErrorCheck(sessionAccount, postDeleteRequestDto);
        if(postErrorType == PostErrorType.POST_NOT_FOUND){
            model.addAttribute(ERROR_TITLE, "게시물 삭제 에러");
            model.addAttribute(ERROR_CONTENT, "존재하지 않는 게시물 입니다.");
            return ERROR_VIEW_NAME;
        }
        if(postErrorType == PostErrorType.NOT_AUTHOR){
            // post 작성자가 아니면,
            model.addAttribute(ERROR_TITLE, "글 삭제 권한 없음");
            model.addAttribute(ERROR_CONTENT, "현재 로그인 되어있는 계정이 삭제하고자 하는 글의 작성자 계정이 아닙니다.");
            return ERROR_VIEW_NAME;
        }
        postService.deletePost(postDeleteRequestDto);
        return REDIRECT + HOME_URL;
    }
}
