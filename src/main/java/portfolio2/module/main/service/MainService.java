package portfolio2.module.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class MainService {

    private final PostRepository postRepository;

    public List<Post> findPostByKeyword(String keyword) {
        return postRepository.findByKeyword(keyword);
    }
}
