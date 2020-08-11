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
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

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
    private final TagRepository tagRepository;

    private final Random random = new Random(System.currentTimeMillis());
    private final List<Post> alreadyUpdatePosts = new ArrayList<>();

    public boolean allAccountsExist() {
        return accountRepository.existsByUserId("shineb523")
                && accountRepository.existsByUserId("rschbh12")
                && accountRepository.existsByUserId("rschbh13")
                && accountRepository.existsByUserId("test");
    }

    private List<Account> getAllAccountsForTest() {
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

    public void generateTestDataRandomly(int taskCount) {
        List<Account> allAccounts = this.getAllAccountsForTest();
        List<Tag> allTags = this.getAllTags(allAccounts);

        for(int count = 1; count <= taskCount; count++){
            Account randomAccount = allAccounts.get(random.nextInt(allAccounts.size()));
            List<Tag> randomTags = this.getRandomTags(allTags);

            int randomTaskTypeNumber = random.nextInt(2);
            switch (randomTaskTypeNumber){
                case 0:
                    // 새로운 게시글 작성
                    this.postNewPostRandomly(randomAccount, randomTags);
                    break;
                case 1:
                    // 기존 게시글에 태그 추가
                    alreadyUpdatePosts.add(this.addRandomTagsRandomly(randomTags));
                    break;
                default:
                    throw new IllegalStateException("randomTaskTypeNumber의 switch문에서 default에 빠짐.");
            }
        }
    }

    private List<Tag> getAllTags(List<Account> allAccounts){
        List<Tag> allTags = new ArrayList<>();
        for (Account account : allAccounts){
            for(Tag tag : account.getInterestTag()){
                if(!allTags.contains(tag))
                    allTags.add(tag);
            }
        }

        for(int i = 0; i < 10; i++){
            String newRandomTagTitle = "랜덤태그" + RandomString.make(3);
            Tag randomTag = tagRepository.findByTitle(newRandomTagTitle);
            if (randomTag == null){
                Tag newRandomTag = new Tag();
                newRandomTag.setTitle(newRandomTagTitle);
                randomTag = tagRepository.save(newRandomTag);
            }
            allTags.add(randomTag);
        }
        return allTags;
    }

    private List<Tag> getRandomTags(List<Tag> allTags) {
        List<Tag> allRandomTags = new ArrayList<>();
        while(allRandomTags.size() < random.nextInt(6)){
            Tag randomTag = allTags.get(random.nextInt(allTags.size()));
            if(!allRandomTags.contains(randomTag))
                allRandomTags.add(randomTag);
        }
        return allRandomTags;
    }

    private void postNewPostRandomly(Account randomAccount, List<Tag> randomTags) {
        PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
        postNewPostRequestDto.setTitle("테스트 글 입니다. " + RandomString.make(4));
        postNewPostRequestDto.setContent(
                "<h2>테스트 내용 입니다.</h2>" +
                        "<p>" + RandomString.make(600) + "</p>" +
                        "<p>" + RandomString.make(300) + "</p>"
        );
        postNewPostRequestDto.setTagTitleOnPost(this.convertTagsListToJoinedString(randomTags));
        Post newPost = postService.saveNewPostWithTag(randomAccount, postNewPostRequestDto);
        postService.sendWebAndEmailNotificationOfNewPost(newPost);
    }

    private String convertTagsListToJoinedString(List<Tag> randomTags) {
        return randomTags.stream().map(Tag::getTitle).collect(Collectors.joining(","));
    }

    private Post addRandomTagsRandomly(List<Tag> randomTags) {
        Post randomPostInDbToUpdate = null;
        while(true) {
            Long randomPostId = (long) random.nextInt(postRepository.findAll().size());
            Post foundPostFromDb = postRepository.findById(randomPostId).orElse(null);
            if (foundPostFromDb == null){
                continue;
            }else{
                if (alreadyUpdatePosts.contains(foundPostFromDb)){
                    if (alreadyUpdatePosts.containsAll(postRepository.findAll()))
                        throw new IllegalStateException("모든 게시물이 이미 업데이트되어, 더이상 업데이트 할 수 없어 무한루프에 빠짐.");
                    continue;
                }else{
                    alreadyUpdatePosts.add(foundPostFromDb);
                    randomPostInDbToUpdate = foundPostFromDb;
                    break;
                }
            }
        }

        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto();
        postUpdateRequestDto.setPostIdToUpdate(randomPostInDbToUpdate.getId());
        postUpdateRequestDto.setTitle(randomPostInDbToUpdate.getTitle());
        postUpdateRequestDto.setContent(randomPostInDbToUpdate.getContent());

        List<Tag> newTags = new ArrayList<>(randomPostInDbToUpdate.getCurrentTag());
        for(Tag randomTag : randomTags){
            if (!newTags.contains(randomTag))
                newTags.add(randomTag);
        }
        postUpdateRequestDto.setTagTitleOnPost(this.convertTagsListToJoinedString(newTags));
        
        Post updatedPost = postService.updatePost(postUpdateRequestDto);
        postService.sendWebAndEmailNotificationOfUpdatedPost(updatedPost);
        return updatedPost;
    }


    public void postRandomly(int totalNumberOfPost) {
        List<Account> allAccounts = this.getAllAccountsForTest();
        List<Tag> allTags = this.getAllTags(allAccounts);

        for (int time = 1; time <= totalNumberOfPost; time++){
            Account randomAccount = allAccounts.get(random.nextInt(allAccounts.size()));
            List<Tag> randomTags = this.getRandomTags(allTags);
            this.postNewPostRandomly(randomAccount, randomTags);
        }
    }

}
