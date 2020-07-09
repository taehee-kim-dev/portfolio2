package portfolio2.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.post.Post;
import portfolio2.domain.post.PostRepository;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.tag.TagRepository;
import portfolio2.dto.request.post.PostNewPostRequestDto;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;


    public Post saveNewPostWithTag(PostNewPostRequestDto postNewPostRequestDto, Account sessionAccount) {
        Account authorAccountInDb = accountRepository.findByUserId(sessionAccount.getUserId());

        Post newPost = modelMapper.map(postNewPostRequestDto, Post.class);
        newPost.setAuthor(authorAccountInDb);
        newPost.setFirstWrittenTime(LocalDateTime.now());

        // 태그 처리
        String[] tagArray = postNewPostRequestDto.getTagTitleOnPost().split(",");

        for(String tagTitle : tagArray){

            Tag existingTagInDb = tagRepository.findByTitle(tagTitle);

            Tag newTagOnPost = null;

            if(existingTagInDb == null){
                // 해당 타이틀로 기존에 데이터베이스에 존재하는 태그가 없으면,

                newTagOnPost = new Tag();
                newTagOnPost.setTitle(tagTitle);

                newTagOnPost = tagRepository.save(newTagOnPost);
            }else{
                newTagOnPost = existingTagInDb;
            }

            newPost.getTag().add(newTagOnPost);
        }

        return postRepository.save(newPost);
    }
}
