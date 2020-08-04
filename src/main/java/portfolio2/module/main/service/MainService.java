package portfolio2.module.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class MainService {

    private final PostRepository postRepository;

    public Page<Post> findPostByKeyword(String keyword, Pageable pageable) {
        return postRepository.findByKeyword(keyword, pageable);
    }
}
