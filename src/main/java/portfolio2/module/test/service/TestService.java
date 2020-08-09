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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Transactional
@RequiredArgsConstructor
@Service
public class TestService {

    private final PostService postService;
    private final AccountRepository accountRepository;

    public void generateTestPostDataWithAuthor(String userIdForTest, int numberOfPost) {
        Account accountForTest = accountRepository.findByUserId(userIdForTest);
        postTestPosts(numberOfPost, accountForTest);
    }

    public boolean isAccountOfUserIdForTestExists(String userIdForTest) {
        return accountRepository.existsByUserId(userIdForTest);
    }

    public void generateTestPostDataRandomly(int totalNumberOfPost) {
        Account account1 = accountRepository.findByUserId("shineb523");
        Account account2 = accountRepository.findByUserId("rschbh12");
        Account account3 = accountRepository.findByUserId("rschbh13");

        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        Random random = new Random(System.currentTimeMillis());

        for (int time = 1; time <= totalNumberOfPost; time++){
            int randomIndexOfAccount = random.nextInt(3);
            Account accountToPost = accounts.get(randomIndexOfAccount);
            this.postTestPosts(1, accountToPost);
        }
    }

    private void postTestPosts(int numberOfPost, Account accountForTest) {
        for (int i = 1; i <= numberOfPost; i++) {
            String randomValueForTitle = RandomString.make(4);
            String randomValue1ForContent = RandomString.make(600);
            String randomValue2ForContent = RandomString.make(300);
            List<String> randomTagTitles = new ArrayList<>();
            for(int j = 0; j < 4; j++){
                randomTagTitles.add(RandomString.make(4));
            }
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("테스트 글 입니다. " + randomValueForTitle);
            postNewPostRequestDto.setContent(
                    "<h2>테스트 내용 입니다.</h2>" +
                    "<p>" + randomValue1ForContent + "</p>" +
                            "<p>" + randomValue2ForContent + "</p>"
            );
            postNewPostRequestDto.setTagTitleOnPost(String.join(",", randomTagTitles) + "," +
                    "계정1의태그1,계정1의태그2," +
                    "계정2의태그1,계정2의태그2,계정2의태그3," +
                    "계정3의태그1,계정3의태그2,계정3의태그3,계정3의태그4");
            Post newPost = postService.saveNewPostWithTag(accountForTest, postNewPostRequestDto);
            postService.sendWebAndEmailNotificationOfNewPost(newPost);
        }
    }
}
