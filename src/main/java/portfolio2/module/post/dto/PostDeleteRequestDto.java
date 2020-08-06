package portfolio2.module.post.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostDeleteRequestDto {

    private Long postIdToDelete;
}
