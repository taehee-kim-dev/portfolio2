package portfolio2.controller.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.CurrentUser;
import portfolio2.domain.post.Post;
import portfolio2.domain.post.PostRepository;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.tag.TagRepository;
import portfolio2.dto.post.PostNewPostRequestDto;
import portfolio2.service.PostService;
import portfolio2.validator.post.PostNewPostRequestDtoValidator;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class PostController {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    private final PostService postService;

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private final String POST_NEW_POST_URL = "/postNewPost";
    private final String POST_NEW_POST_VIEW_NAME = "post/form";

    private final PostNewPostRequestDtoValidator postNewPostRequestDtoValidator;

    @InitBinder("postNewPostRequestDto")
    public void initBinderForPostNewPostRequestDto(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(postNewPostRequestDtoValidator);
    }

    @GetMapping(POST_NEW_POST_URL)
    public String getPostNewPost(@CurrentUser Account sessionAccount, Model model) {
        model.addAttribute("sessionAccount", sessionAccount);
        model.addAttribute(new PostNewPostRequestDto());

        return POST_NEW_POST_VIEW_NAME + "/post-new-post-form";
    }

    @PostMapping(POST_NEW_POST_URL)
    public String postPostNewPost(@CurrentUser Account sessionAccount, @Valid PostNewPostRequestDto postNewPostRequestDto, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("sessionAccount", sessionAccount);
            model.addAttribute(postNewPostRequestDto);

            return POST_NEW_POST_VIEW_NAME + "/post-new-post-form";
        }

        Post newPostInDb = postService.saveNewPost(modelMapper.map(postNewPostRequestDto, Post.class), sessionAccount);

        return "redirect:/post/" + newPostInDb.getId();
    }

    @GetMapping("/post/{postId}")
    public String showPost(@CurrentUser Account sessionAccount,
                           @PathVariable Long postId,
                           Model model) {

        model.addAttribute("sessionAccount", sessionAccount);

        Post foundPost = postRepository.findById(postId).get();


        model.addAttribute(foundPost);
        model.addAttribute("firstWrittenTime", foundPost.getFirstWrittenTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("isAuthor", sessionAccount.getUserId().equals(foundPost.getAuthor().getUserId()));

        return "post/post-view";
    }
}
