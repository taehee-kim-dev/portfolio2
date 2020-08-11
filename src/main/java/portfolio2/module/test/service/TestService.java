package portfolio2.module.test.service;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.post.service.process.PostProcess;
import portfolio2.module.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class TestService {

    private final PostService postService;
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final PostProcess postProcess;
    private final int TOTAL_ACCOUNTS_NUMBER = 4;

    public boolean allAccountsExist() {
        return accountRepository.existsByUserId("shineb523")
                && accountRepository.existsByUserId("rschbh12")
                && accountRepository.existsByUserId("rschbh13")
                && accountRepository.existsByUserId("test");
    }

    private List<Account> getAccountsForTest() {
        Account account1 = accountRepository.findByUserId("shineb523");
        Account account2 = accountRepository.findByUserId("rschbh12");
        Account account3 = accountRepository.findByUserId("rschbh13");
        Account account4 = accountRepository.findByUserId("test");

        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        accounts.add(account4);
        return accounts;
    }

    public void postRandomly(int totalNumberOfPost) {
        List<Account> accounts = getAccountsForTest();

        Random random = new Random(System.currentTimeMillis());

        for (int time = 1; time <= totalNumberOfPost; time++){
            int randomIndexOfAccount = random.nextInt(TOTAL_ACCOUNTS_NUMBER);
            Account accountToPost = accounts.get(randomIndexOfAccount);
            this.postTestPosts(accountToPost);
        }
    }

    private void postTestPosts(Account accountForTest) {
        PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
        postNewPostRequestDto.setTitle("테스트 글 입니다. " + RandomString.make(4));
        postNewPostRequestDto.setContent(
                "<h2>테스트 내용 입니다.</h2>" +
                        "<p>" + RandomString.make(600) + "</p>" +
                        "<p>" + RandomString.make(300) + "</p>"
        );
        postNewPostRequestDto.setTagTitleOnPost(this.getRandomTagTitlesInStringForPostingNewPost());
        Post newPost = postService.saveNewPostWithTag(accountForTest, postNewPostRequestDto);
        postService.sendWebAndEmailNotificationOfNewPost(newPost);
    }

    private String getRandomTagTitlesInStringForPostingNewPost() {
        List<String> allTagTitles = getAllBasicTagTitles();

        Random random = new Random(System.currentTimeMillis());
        // 태그타이틀 0개~5개 랜덤 선택
        List<String> tagTitlesList = new ArrayList<>();
        int randomTotalSizeOfTags = random.nextInt(6);
        while(tagTitlesList.size() < randomTotalSizeOfTags){
            int randomIndexOfTagTitle = random.nextInt(allTagTitles.size());
            String tagTitle = allTagTitles.get(randomIndexOfTagTitle);
            if (!tagTitlesList.contains(tagTitle))
                tagTitlesList.add(tagTitle);
        }
        return String.join(",", tagTitlesList);
    }

    private String getRandomTagTitlesInStringForUpdatingPost(List<String> currentTag) {
        List<String> allTagTitles = getAllBasicTagTitles();

        Random random = new Random(System.currentTimeMillis());
        for(int time = 1; time <= 5; time ++){
            int randomIndexOfTagTitle = random.nextInt(allTagTitles.size());
            String tagTitle = allTagTitles.get(randomIndexOfTagTitle);
            if (!currentTag.contains(tagTitle))
                currentTag.add(tagTitle);
        }
        return String.join(",", currentTag);
    }

    private List<String> getAllBasicTagTitles() {
        List<String> allTagTitles = new ArrayList<>();
        for(int accountNumber = 1; accountNumber <= TOTAL_ACCOUNTS_NUMBER - 1; accountNumber++){
            for(int tagNumber = 1; tagNumber <= 5; tagNumber++){
                allTagTitles.add("계정" + accountNumber + "의 태그" + tagNumber);
            }
        }

        for(int tagNumber = 1; tagNumber <= 5; tagNumber++){
            allTagTitles.add("테스트용계정의 태그" + tagNumber);
        }


        for (int i = 0; i < 7; i++){
            allTagTitles.add(RandomString.make(5));
        }
        return allTagTitles;
    }

    public void addTagsToPostsRandomly() {
        List<Account> accounts = getAccountsForTest();

        for(Account account : accounts){
            List<Post> allPosts = postRepository.findAllByAuthor(account);
            for (Post post : allPosts){
                PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto();
                postUpdateRequestDto.setPostIdToUpdate(post.getId());
                postUpdateRequestDto.setTagTitleOnPost(
                        this.getRandomTagTitlesInStringForUpdatingPost(
                                post.getCurrentTag().stream().map(Tag::getTitle).collect(Collectors.toList())));
                Post updatedPost = postProcess.updateTagOfPost(post, postUpdateRequestDto);
                postProcess.sendNotificationAboutUpdatedPost(updatedPost);
            }
        }
    }
}
