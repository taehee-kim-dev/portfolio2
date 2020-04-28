package portfolio2.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.TestAccountInfo;
import portfolio2.WithAccount;

@SpringBootTest
@AutoConfigureMockMvc
public class LogInAndOutTest {

    @Autowired
    private MockMvc mockMvc;

    @WithAccount(TestAccountInfo.CORRECT_TEST_USER_ID)
    @Test
    void logInWithCorrectInput(){

    }
}
