package portfolio2.dto.post;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostNewPostRequestDto {

    private String title;

    private String content;

    private String tagTitleOnPost;
}
