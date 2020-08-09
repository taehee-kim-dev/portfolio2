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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account author;

    private String title;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String content;

    @ManyToMany
    private List<Tag> currentTag = new ArrayList<>();

    @ManyToMany
    private List<Tag> beforeTag = new ArrayList<>();

    private LocalDateTime firstWrittenDateTime;

    private LocalDateTime lastModifiedDateTime;

    public void updateTitleAndContentAndDate(PostUpdateRequestDto postUpdateRequestDto) {
        this.title = postUpdateRequestDto.getTitle();
        this.content = postUpdateRequestDto.getContent();
        this.lastModifiedDateTime = LocalDateTime.now();
    }
}
