package portfolio2.module.post;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import portfolio2.module.account.Account;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.tag.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Post.withCurrentTag", attributeNodes = {
        @NamedAttributeNode("currentTag")
})
@Entity
public class Post {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account author;

    private String title;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String content;

    @ManyToMany
    private List<Tag> currentTag = new LinkedList<>();

    @ManyToMany
    private List<Tag> beforeTag = new LinkedList<>();

    private LocalDateTime firstWrittenDateTime;

    private LocalDateTime lastModifiedDateTime;

    public void updateTitleAndContent(PostUpdateRequestDto postUpdateRequestDto) {
        this.title = postUpdateRequestDto.getTitle();
        this.content = postUpdateRequestDto.getContent();
    }
}
