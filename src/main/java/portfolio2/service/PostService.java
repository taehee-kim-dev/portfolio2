package portfolio2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.post.Post;
import portfolio2.domain.post.PostRepository;
import portfolio2.domain.process.post.PostProcess;
import portfolio2.dto.request.post.PostRequestDto;

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
