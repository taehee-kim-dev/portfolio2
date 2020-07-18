package portfolio2.module.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.event.PostEventType;
import portfolio2.module.post.service.process.PostProcess;
import portfolio2.module.tag.Tag;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostProcess postProcess;
    private final PostRepository postRepository;

    public Post saveNewPostWithTag(Account sessionAccount, PostNewPostRequestDto postRequestDto) {
        Post savedPostInDb = postProcess.saveNewPost(sessionAccount, postRequestDto);
        return postProcess.addTagToNewPost(savedPostInDb, postRequestDto);
    }

    public void sendWebAndEmailNotificationOfNewPost(Post newPost){
        postProcess.sendWebAndEmailNotificationAboutTag(newPost, PostEventType.NEW);
    }

    public Post updatePost(PostUpdateRequestDto postUpdateRequestDto) {
        Post postInDbToUpdate = postRepository.findById(postUpdateRequestDto.getPostIdToUpdate()).orElseThrow(IllegalArgumentException::new);
        postInDbToUpdate.updateTitleAndContent(postUpdateRequestDto);
        return  postProcess.updateTagOfPost(postInDbToUpdate, postUpdateRequestDto);
    }

    public void sendWebAndEmailNotificationOfUpdatedPost(Post updatedPost){
        postProcess.sendWebAndEmailNotificationAboutTag(updatedPost, PostEventType.UPDATED);
    }
}
