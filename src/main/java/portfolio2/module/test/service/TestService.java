package portfolio2.module.test.service;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.post.Post;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.service.PostService;

@Transactional
@RequiredArgsConstructor
@Service
public class TestService {

    private final PostService postService;
    private final AccountRepository accountRepository;

    public void generateTestPostDataWithAuthor(String userIdForTest, int numberOfPost) {
        Account accountForTest = accountRepository.findByUserId(userIdForTest);
        for (int i = 1; i <= numberOfPost; i++) {
            String randomValue = RandomString.make(4);
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("테스트 글 입니다. " + randomValue);
            postNewPostRequestDto.setContent(
                    "<h1>테스트 내용 입니다.</h1>" +
                    "<h2>Where does it come from?</h2>" +
                    "<p>Contrary to popular belief, Lorem Ipsum is not" +
                    " simply random text. It has roots in a piece of c" +
                    "lassical Latin literature from 45 BC, making it o" +
                    "ver 2000 years old. Richard McClintock, a Latin p" +
                    "rofessor at Hampden-Sydney College in Virginia, l" +
                    "ooked up one of the more obscure Latin words, con" +
                    "sectetur, from a Lorem Ipsum passage, and going t" +
                    "hrough the cites of the word in classical literat" +
                    "ure, discovered the undoubtable source. Lorem Ips" +
                    "um comes from sections 1.10.32 and 1.10.33 of \"d" +
                    "e Finibus Bonorum et Malorum\" (The Extremes of G" +
                    "ood and Evil) by Cicero, written in 45 BC. This b" +
                    "ook is a treatise on the theory of ethics, very p" +
                    "opular during the Renaissance. The first line of " +
                    "Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", co" +
                    "mes from a line in section 1.10.32.</p>" +
                            "<p>The standard chunk of Lorem Ipsum used since the 1500s is " +
                            "reproduced below for those interested. Sections 1.10.32 and 1" +
                            ".10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are a" +
                            "lso reproduced in their exact original form, accompanied by E" +
                            "nglish versions from the 1914 translation by H. Rackham.</p>"
            );
            postNewPostRequestDto.setTagTitleOnPost("태그1, 태그2, 태그3, 태그4");
            Post newPost = postService.saveNewPostWithTag(accountForTest, postNewPostRequestDto);
            postService.sendWebAndEmailNotificationOfNewPost(newPost);
        }
    }

    public boolean isAccountOfUserIdForTestExists(String userIdForTest) {
        return accountRepository.existsByUserId(userIdForTest);
    }
}
