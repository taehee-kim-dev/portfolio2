package portfolio2.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.post.Post;
import portfolio2.domain.post.PostRepository;
import portfolio2.domain.process.post.PostProcess;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.tag.TagRepository;
import portfolio2.dto.request.post.PostNewPostRequestDto;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostProcess postProcess;
    private final PostRepository postRepository;

    public Post saveNewPostWithTag(Account sessionAccount, PostNewPostRequestDto postNewPostRequestDto) {
        Post savedPostInDb = postProcess.saveNewPost(sessionAccount, postNewPostRequestDto);
        return postProcess.addTagToNewPost(savedPostInDb, postNewPostRequestDto);
    }

    public Post findPost(Long postId) {
        Optional<Post> foundPostFromDb = postRepository.findById(postId);
        return foundPostFromDb.orElse(null);
    }
}
