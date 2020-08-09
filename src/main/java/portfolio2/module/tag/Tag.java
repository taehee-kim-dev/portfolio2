package portfolio2.module.tag;

import lombok.*;
import portfolio2.module.account.Account;
import portfolio2.module.notification.Notification;
import portfolio2.module.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @ManyToMany(mappedBy = "interestTag")
    private List<Account> accounts = new ArrayList<>();

    @ManyToMany(mappedBy = "currentTag")
    private List<Post> postsOfCurrentTags = new ArrayList<>();

    @ManyToMany(mappedBy = "beforeTag")
    private List<Post> postsOfBeforeTags = new ArrayList<>();

    @ManyToMany(mappedBy = "commonTag")
    private List<Notification> notifications = new ArrayList<>();
}
