package portfolio2.module.main.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.post.PostRepository;
import portfolio2.module.tag.TagRepository;

/**
 * 제목, 내용, 태그로 찾아지는지?
 * 최신순으로 정렬되어있는지?
 * */

@MockMvcTest
public class PostSearchTest  extends ContainerBaseTest {


}
