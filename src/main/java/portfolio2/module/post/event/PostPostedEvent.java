package portfolio2.module.post.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import portfolio2.module.post.Post;
import portfolio2.module.tag.Tag;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class PostPostedEvent{

    @Enumerated(EnumType.STRING)
    private PostEventType postEventType;

    private Post newPost;
}
