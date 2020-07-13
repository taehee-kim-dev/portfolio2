package portfolio2.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.config.SessionAccount;
import portfolio2.domain.post.Post;
import portfolio2.domain.tag.Tag;
import portfolio2.dto.request.post.PostNewPostRequestDto;
import portfolio2.service.PostService;
import portfolio2.validator.post.PostNewPostRequestDtoValidator;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.controller.config.UrlAndViewNameAboutAccount.NOT_FOUND_ERROR_VIEW_NAME;
import static portfolio2.controller.config.UrlAndViewNameAboutBasic.REDIRECT;
import static portfolio2.controller.config.UrlAndViewNameAboutPost.*;

@RequiredArgsConstructor
@Controller
public class PostController {
    private final PostService postService;

    private final PostNewPostRequestDtoValidator postNewPostRequestDtoValidator;

    @InitBinder("postNewPostRequestDto")
    public void initBinderForPostNewPostRequestDto(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(postNewPostRequestDtoValidator);
    }

    @GetMapping(POST_NEW_POST_URL)
    public String showPostNewPostView(@SessionAccount Account sessionAccount, Model model) {
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute(new PostNewPostRequestDto());
        return POST_NEW_POST_VIEW_NAME;
    }

    @PostMapping(POST_NEW_POST_URL)
    public String postPostNewPost(@SessionAccount Account sessionAccount,
                                  @Valid @ModelAttribute PostNewPostRequestDto postNewPostRequestDto,
                                  Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(postNewPostRequestDto);
            return POST_NEW_POST_VIEW_NAME;
        }
        Post savedNewPostInDb = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
        return REDIRECT + POST_VIEW_URL + '/' + savedNewPostInDb.getId();
    }

    @GetMapping(POST_VIEW_URL + "/{postId}")
    public String showPost(@SessionAccount Account sessionAccount,
                           @PathVariable Long postId,
                           Model model) {
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        Post foundPostInDb = postService.findPost(postId);
        if(foundPostInDb == null){
            model.addAttribute("notFoundErrorTitle", "게시물 조회 에러");
            model.addAttribute("notFoundErrorContent", "존재하지 않는 게시물 입니다.");
            return NOT_FOUND_ERROR_VIEW_NAME;
        }
        model.addAttribute(foundPostInDb);
        model.addAttribute("isAuthor", sessionAccount.getUserId().equals(foundPostInDb.getAuthor().getUserId()));
        model.addAttribute("firstWrittenTime", foundPostInDb.getFirstWrittenTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("tagOnPost", foundPostInDb.getTag().stream().map(Tag::getTitle).collect(Collectors.toList()));
        return POST_VIEW_NAME;
    }
}
