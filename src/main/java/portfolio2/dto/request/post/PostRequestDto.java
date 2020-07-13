package portfolio2.dto.request.post;

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
