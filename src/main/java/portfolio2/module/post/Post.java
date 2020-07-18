package portfolio2.module.post;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import portfolio2.module.account.Account;
import portfolio2.module.tag.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Post.withTag", attributeNodes = {
        @NamedAttributeNode("tag")
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
    private List<Tag> tag = new ArrayList<>();

    private LocalDateTime firstWrittenTime;

    private LocalDateTime lastModifiedTime;
}
