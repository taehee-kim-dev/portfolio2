package portfolio2.domain.post;

import lombok.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.tag.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Post.withAllRelation", attributeNodes = {
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
    private Set<Tag> tag  = new HashSet<>();

    private LocalDateTime firstWrittenTime;

    private LocalDateTime lastModifiedTime;
}
