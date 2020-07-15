package portfolio2.module.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostRequestDto;
import portfolio2.module.post.service.process.PostProcess;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostProcess postProcess;
    private final PostRepository postRepository;

    public Post saveNewPostWithTag(Account sessionAccount, PostRequestDto postRequestDto) {
        Post savedPostInDb = postProcess.saveNewPost(sessionAccount, postRequestDto);
        return postProcess.addTagToNewPost(savedPostInDb, postRequestDto);
    }

    public Post findPost(Long postId) {
        Optional<Post> foundPostFromDb = postRepository.findById(postId);
        return foundPostFromDb.orElse(null);
    }
}
