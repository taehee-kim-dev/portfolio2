package portfolio2.module.post.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostRequestDto {

    private String title;

    private String content;

    private String tagTitleOnPost;
}
