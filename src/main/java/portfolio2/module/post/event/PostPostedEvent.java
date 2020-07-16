package portfolio2.module.post.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import portfolio2.module.post.Post;

@Getter
@RequiredArgsConstructor
public class PostPostedEvent{

    private final Post post;
}
