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
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("테스트 글 입니다. " + RandomString.make(4));
            postNewPostRequestDto.setContent(
                    "<h2>테스트 내용 입니다.</h2>" +
                    "<p>" + RandomString.make(600) + "</p>" +
                            "<p>" + RandomString.make(300) + "</p>"
            );
            postNewPostRequestDto.setTagTitleOnPost(this.getRandomTagTitles());
            Post newPost = postService.saveNewPostWithTag(accountForTest, postNewPostRequestDto);
            postService.sendWebAndEmailNotificationOfNewPost(newPost);
        }
    }

    private String getRandomTagTitles() {
        List<String> allTagTitles = new ArrayList<>();
        for(int accountNumber = 1; accountNumber <= 3; accountNumber++){
            for(int tagNumber = 1; tagNumber <= 5; tagNumber++){
                allTagTitles.add("계정" + accountNumber + "의태그" + tagNumber);
            }
        }

        for (int i = 0; i < 7; i++){
            allTagTitles.add(RandomString.make(5));
        }

        Random random = new Random(System.currentTimeMillis());
        // 태그타이틀 0개~5개 랜덤 선택
        int randomTotalSizeOfTags = random.nextInt(6);
        List<String> tagTitlesList = new ArrayList<>();
        for(int currentSize = 1; currentSize <= randomTotalSizeOfTags; currentSize++){
            int randomIndexOfTagTitle = random.nextInt(allTagTitles.size());
            String tagTitle = allTagTitles.get(randomIndexOfTagTitle);
            if (!tagTitlesList.contains(tagTitle))
                tagTitlesList.add(tagTitle);
        }
        return String.join(",", tagTitlesList);
    }
}
