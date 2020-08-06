package portfolio2.module.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.post.Post;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.service.PostService;

@Transactional
@RequiredArgsConstructor
@Service
public class TestService {

    private final PostService postService;

    public void generateTestPostData(Account sessionAccount) {
        for (int i = 1; i <= 34; i++) {
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("테스트 글 " + i);
            postNewPostRequestDto.setContent("테스트용 글 입니다.");
            postNewPostRequestDto.setTagTitleOnPost("1,2,3");
            Post newPost = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
            postService.sendWebAndEmailNotificationOfNewPost(newPost);
        }
    }
}
