package portfolio2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.post.Post;
import portfolio2.domain.post.PostRepository;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final AccountRepository accountRepository;
    private final PostRepository postRepository;


    public Post saveNewPost(Post newPost, Account sessionAccount) {
        Account authorAccountInDb = accountRepository.findByUserId(sessionAccount.getUserId());

        newPost.setAuthor(authorAccountInDb);
        newPost.setFirstWrittenTime(LocalDateTime.now());

        Post newPostInDb = postRepository.save(newPost);

        return newPostInDb;
    }
}
