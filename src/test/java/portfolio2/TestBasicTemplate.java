//package portfolio2;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import portfolio2.domain.account.AccountRepository;
//import portfolio2.mail.EmailService;
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//public class TestBasicTemplate {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @MockBean
//    private EmailService emailService;
//
//    @AfterEach
//    void afterEach(){
//        accountRepository.deleteAll();
//    }
//}
