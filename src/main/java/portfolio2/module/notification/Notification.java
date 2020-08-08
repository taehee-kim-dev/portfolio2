package portfolio2.module.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import portfolio2.module.account.Account;
import portfolio2.module.tag.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@NamedEntityGraph(
        name = "Notification.withCommonTag",
        attributeNodes = @NamedAttributeNode("commonTag")
)
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String title;

    private String link;

    private boolean ringBellChecked = false;

    private boolean linkVisited = false;

    @ManyToOne
    private Account account;

    @ManyToMany
    private List<Tag> commonTag = new LinkedList<>();

    private LocalDateTime createdDateTime;
}
