package portfolio2.module.search.post;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpLogInEmailVerificationCustomProcessForTest;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.search.service.SearchService;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.SESSION_ACCOUNT;
import static portfolio2.module.search.controller.config.StaticVariableNamesAboutSearch.SEARCH_POST_RESULT_VIEW_NAME;
import static portfolio2.module.search.controller.config.StaticVariableNamesAboutSearch.SEARCH_POST_URL;

/**
 * 작성자 아이디, 닉네임, 글의 제목, 내용, 태그로 찾아지는지?
 * 최신순으로 정렬되어있는지?
 * */


@MockMvcTest
public class PostSearchByAllFactorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private SignUpLogInEmailVerificationCustomProcessForTest signUpLogInEmailVerificationCustomProcessForTest;

    @Autowired
    private SearchService searchService;

    @Autowired
    private PostService postService;

    private final String KEY_WORD_TO_SEARCH_65 = "wovcub";

    PageRequest page0 = PageRequest.of(0, 15, Sort.Direction.DESC, "firstWrittenDateTime");
    PageRequest page1 = PageRequest.of(1, 15, Sort.Direction.DESC, "firstWrittenDateTime");
    PageRequest page2 = PageRequest.of(2, 15, Sort.Direction.DESC, "firstWrittenDateTime");
    PageRequest page3 = PageRequest.of(3, 15, Sort.Direction.DESC, "firstWrittenDateTime");
    PageRequest page4 = PageRequest.of(4, 15, Sort.Direction.DESC, "firstWrittenDateTime");

    private final LocalDateTime STANDARD_TIME = LocalDateTime.now();

    @AfterEach
    void afterEach(){
        postRepository.deleteAll();
        tagRepository.deleteAll();
        accountRepository.deleteAll();
    }


    private void createPosts(int eachPostNumber){
        Account account2 = signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        int count = 0;
        // 제목에 키워드 포함
        for(int i = eachPostNumber; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();

            String randomValueForTitle = RandomString.make(5);
            postNewPostRequestDto.setTitle(KEY_WORD_TO_SEARCH_65 + randomValueForTitle);

            postNewPostRequestDto.setContent("Test content not contains keyword");
            postNewPostRequestDto.setTagTitleOnPost("Tag1" + ',' + "Tag2");

            Post postInDb = postService.saveNewPostWithTag(account2, postNewPostRequestDto);
            postInDb.setFirstWrittenDateTime(STANDARD_TIME.minusHours(1).minusMinutes(i));
            postRepository.save(postInDb);

            count += 1;
            // System.out.println("현재까지 생성된 post 개수 : " + count);
        }

        // 내용에 키워드 포함
        for(int i = eachPostNumber; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");

            String randomValueForContent = RandomString.make(5);
            postNewPostRequestDto.setContent(KEY_WORD_TO_SEARCH_65 + randomValueForContent);

            postNewPostRequestDto.setTagTitleOnPost("Tag3" + ',' + "Tag4");

            Post postInDb = postService.saveNewPostWithTag(account2, postNewPostRequestDto);
            postInDb.setFirstWrittenDateTime(STANDARD_TIME.minusHours(2).minusMinutes(i));
            postRepository.save(postInDb);

            count += 1;
            // System.out.println("현재까지 생성된 post 개수 : " + count);
        }

        // 태그에 키워드 포함
        for(int i = eachPostNumber; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");
            postNewPostRequestDto.setContent("Test content not contains keyword");

            String randomValueForTag1 = RandomString.make(3);
            String randomValueForTag2 = RandomString.make(3);
            postNewPostRequestDto.setTagTitleOnPost(KEY_WORD_TO_SEARCH_65 + randomValueForTag1
                    + ',' + KEY_WORD_TO_SEARCH_65 + randomValueForTag2);

            Post postInDb = postService.saveNewPostWithTag(account2, postNewPostRequestDto);
            postInDb.setFirstWrittenDateTime(STANDARD_TIME.minusHours(3).minusMinutes(i));
            postRepository.save(postInDb);

            count += 1;
            // System.out.println("현재까지 생성된 post 개수 : " + count);
        }

        // 어느 키워드도 포함되지 않는 글
        for(int i = eachPostNumber; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");
            postNewPostRequestDto.setContent("Test content not contains keyword");
            postNewPostRequestDto.setTagTitleOnPost("Tag5" + ',' + "Tag6");

            Post postInDb = postService.saveNewPostWithTag(account2, postNewPostRequestDto);
            postInDb.setFirstWrittenDateTime(STANDARD_TIME.minusHours(4).minusMinutes(i));
            postRepository.save(postInDb);

            count += 1;
            System.out.println("현재까지 생성된 post 개수 : " + count);
        }

        logInAndOutProcessForTest.logOut();

        // 아이디에 키워드 포함
        for(int i = eachPostNumber; i >= 1; i--){
            String randomValue = RandomString.make(4).toLowerCase();
            Account accountWithKeywordUserId = signUpLogInEmailVerificationCustomProcessForTest
                    .withCustomProperties(
                            randomValue + KEY_WORD_TO_SEARCH_65,
                            randomValue,
                            randomValue + "@email.com",
                            "asdfasdf",
                            true,
                            true);

            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");
            postNewPostRequestDto.setContent("Test content not contains keyword");
            postNewPostRequestDto.setTagTitleOnPost("Tag7" + ',' + "Tag8");

            Post postInDb = postService.saveNewPostWithTag(accountWithKeywordUserId, postNewPostRequestDto);
            postInDb.setFirstWrittenDateTime(STANDARD_TIME.minusHours(5).minusMinutes(i));
            postRepository.save(postInDb);
            logInAndOutProcessForTest.logOut();

            count += 1;
            // System.out.println("현재까지 생성된 post 개수 : " + count);
        }

        // 닉네임에 키워드 포함
        for(int i = eachPostNumber; i >= 1; i--){
            String randomValue = RandomString.make(4).toLowerCase();
            Account accountWithKeywordNickname = signUpLogInEmailVerificationCustomProcessForTest
                    .withCustomProperties(
                            randomValue,
                            randomValue + KEY_WORD_TO_SEARCH_65,
                            randomValue + "@email.com",
                            "asdfasdf",
                            true,
                            true);

            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");
            postNewPostRequestDto.setContent("Test content not contains keyword");
            postNewPostRequestDto.setTagTitleOnPost("Tag9" + ',' + "Tag10");

            Post postInDb = postService.saveNewPostWithTag(accountWithKeywordNickname, postNewPostRequestDto);
            postInDb.setFirstWrittenDateTime(STANDARD_TIME.minusHours(6).minusMinutes(i));
            postRepository.save(postInDb);
            logInAndOutProcessForTest.logOut();

            count += 1;
            // System.out.println("현재까지 생성된 post 개수 : " + count);
        }
    }

    /**
     * (0 ~ 4) / 5 = 0 -> 0 ~ 4 but max = 4
     * (5 ~ 9) / 5 = 1 -> 5 ~ 9 but max = 9
     * (10 ~ 14) / 5 = 2 -> 10 ~ 14 but max = 14
     * (15 ~ 19) / 5 = 3 -> 15 ~ 19 but max = 19
     * (20 ~ 24) / 5 = 4 -> 20 ~ 24 but max = 24
     * */
    private void searchPagingIndexRangeTest(int totalPage, int pageToShow) throws Exception {

        assertTrue(totalPage >= pageToShow);

        int totalPageIndex = totalPage - 1;
        int pageIndexToShow = pageToShow - 1;

        int currentPageRangeFirstIndex = pageIndexToShow / 5 * 5;
        int currentPageFullRangeLastIndex = currentPageRangeFirstIndex + 4;
        int currentPageRangeLastIndex = Math.min(totalPageIndex, currentPageFullRangeLastIndex);

        int currentPageRangeLastNumber = currentPageRangeLastIndex + 1;

        if (1 <= pageToShow && pageToShow <= 5){
            if (totalPage >= 5){
                assertEquals(5, currentPageRangeLastNumber);
            }else{
                assertEquals(totalPage, currentPageRangeLastNumber);
            }
        }else if (6 <= pageToShow && pageToShow <= 10){
            if (totalPage >= 10){
                assertEquals(10, currentPageRangeLastNumber);
            }else{
                assertEquals(totalPage, currentPageRangeLastNumber);
            }
        }else if (11 <= pageToShow && pageToShow <= 15){
            if (totalPage >= 15){
                assertEquals(15, currentPageRangeLastNumber);
            }else{
                assertEquals(totalPage, currentPageRangeLastNumber);
            }
        }else if (16 <= pageToShow && pageToShow <= 20){
            if (totalPage >= 20){
                assertEquals(20, currentPageRangeLastNumber);
            }else{
                assertEquals(totalPage, currentPageRangeLastNumber);
            }
        }else if (21 <= pageToShow && pageToShow <= 25){
            if (totalPage >= 25){
                assertEquals(25, currentPageRangeLastNumber);
            }else{
                assertEquals(totalPage, currentPageRangeLastNumber);
            }
        }
    }


    @DisplayName("Paging index test")
    @Test
    void pagingIndexTest() throws Exception {
        this.searchPagingIndexRangeTest(1, 1);
        this.searchPagingIndexRangeTest(3, 2);
        this.searchPagingIndexRangeTest(5, 3);
        this.searchPagingIndexRangeTest(7, 1);
        this.searchPagingIndexRangeTest(7, 3);
        this.searchPagingIndexRangeTest(7, 5);
        this.searchPagingIndexRangeTest(7, 6);
        this.searchPagingIndexRangeTest(7, 7);
        this.searchPagingIndexRangeTest(10, 1);
        this.searchPagingIndexRangeTest(10, 3);
        this.searchPagingIndexRangeTest(10, 5);
        this.searchPagingIndexRangeTest(10, 7);
        this.searchPagingIndexRangeTest(10, 9);
        this.searchPagingIndexRangeTest(10, 10);
        this.searchPagingIndexRangeTest(15, 6);
        this.searchPagingIndexRangeTest(15, 8);
        this.searchPagingIndexRangeTest(15, 14);
        this.searchPagingIndexRangeTest(17, 1);
        this.searchPagingIndexRangeTest(17, 3);
        this.searchPagingIndexRangeTest(17, 5);
        this.searchPagingIndexRangeTest(17, 6);
        this.searchPagingIndexRangeTest(17, 8);
        this.searchPagingIndexRangeTest(17, 9);
        this.searchPagingIndexRangeTest(17, 10);
        this.searchPagingIndexRangeTest(17, 13);
        this.searchPagingIndexRangeTest(17, 15);
        this.searchPagingIndexRangeTest(17, 17);

        for (int i = 0; i < 10000; i++){
            Random random = new Random(System.currentTimeMillis());
            int totalPage = random.nextInt(30) + 1; //  1 ~ 31
            int pageToShow = random.nextInt(25) + 1; // 1 ~ 25
            if(totalPage >= pageToShow){
                this.searchPagingIndexRangeTest(totalPage, pageToShow);
            }
        }
    }


    // 처음 검색

    @DisplayName("Post검색 - 비로그인 상태 - 존재하는 키워드로 검색 - Controller단 - 처음 검색")
    @Test
    void searchPostNotLoggedInWithKeywordControllerInitialSearch() throws Exception{
        this.createPosts(8);
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "  " + KEY_WORD_TO_SEARCH_65))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", KEY_WORD_TO_SEARCH_65))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());
    }


    @DisplayName("Post검색 - 비로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단 - 처음 검색")
    @Test
    void searchPostNotLoggedInWithNotKeywordControllerInitialSearch() throws Exception{
        this.createPosts(8);
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "ASgvcb   "))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", "ASgvcb"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하는 키워드로 검색 - Controller단 - 처음 검색")
    @Test
    void searchPostLoggedInWithKeywordControllerInitialSearch() throws Exception{
        this.createPosts(8);
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "  " + KEY_WORD_TO_SEARCH_65 + "   "))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", KEY_WORD_TO_SEARCH_65))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단 - 처음 검색")
    @Test
    void searchPostLoggedInWithNotKeywordControllerInitialSearch() throws Exception{
        this.createPosts(8);
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "XCNhgf"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", "XCNhgf"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    // 페이징

    @DisplayName("Post검색 - 비로그인 상태 - 존재하는 키워드로 검색 - Controller단 - 페이징")
    @Test
    void searchPostNotLoggedInWithKeywordControllerPaging() throws Exception{
        this.createPosts(8);
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "  " + KEY_WORD_TO_SEARCH_65)
                .param("sort", "firstWrittenDateTime,desc")
                .param("page", "2"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", KEY_WORD_TO_SEARCH_65))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());
    }


    @DisplayName("Post검색 - 비로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단 - 페이징")
    @Test
    void searchPostNotLoggedInWithNotKeywordControllerPaging() throws Exception{
        this.createPosts(8);
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "ASgvcb   ")
                .param("sort", "firstWrittenDateTime,desc")
                .param("page", "2"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", "ASgvcb"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하는 키워드로 검색 - Controller단 - 페이징")
    @Test
    void searchPostLoggedInWithKeywordControllerPaging() throws Exception{
        this.createPosts(8);
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "  " + KEY_WORD_TO_SEARCH_65 + "   ")
                .param("sort", "firstWrittenDateTime,desc")
                .param("page", "2"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", KEY_WORD_TO_SEARCH_65))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단 - 페이징")
    @Test
    void searchPostLoggedInWithNotKeywordControllerPaging() throws Exception{
        this.createPosts(8);
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "XCNhgf")
                .param("sort", "firstWrittenDateTime,desc")
                .param("page", "2"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("keyword", "XCNhgf"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(model().attributeExists("currentPageRangeFirstIndex"))
                .andExpect(model().attributeExists("currentPageRangeLastIndex"))
                .andExpect(model().attribute("sortProperty", "firstWrittenDateTime"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }


    @Commit
    @DisplayName("Post검색 - 존재하는 키워드로 검색 - 65개의 결과 - Service단")
    @Test
    void searchPostWithKeywordService() throws Exception{
        this.createPosts(13);
        Page<Post> page0Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_65, page0);
        Page<Post> page1Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_65, page1);
        Page<Post> page2Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_65, page2);
        Page<Post> page3Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_65, page3);
        Page<Post> page4Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_65, page4);

        assertEquals(65L, page0Elements.getTotalElements());

        List<Post> page0List = page0Elements.getContent();
        assertEquals(15, page0List.size());

        List<Post> page1List = page1Elements.getContent();
        assertEquals(15, page1List.size());

        List<Post> page2List = page2Elements.getContent();
        assertEquals(15, page2List.size());

        List<Post> page3List = page3Elements.getContent();
        assertEquals(15, page3List.size());

        List<Post> page4List = page4Elements.getContent();
        assertEquals(5, page4List.size());

        assertFalse(page4Elements.hasNext());

        LocalDateTime timeOfBeforePost = LocalDateTime.MAX;

        for (Post post : page0List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_65));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }

        for (Post post : page1List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_65));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }

        for (Post post : page2List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_65));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }

        for (Post post : page3List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_65));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }

        for (Post post : page4List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_65));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }
    }

    private boolean isPostHasKeyword(Post post, String keyword) {
        return post.getAuthor().getUserId().contains(keyword)
                || post.getAuthor().getNickname().contains(keyword)
                || post.getTitle().contains(keyword)
                || post.getContent().contains(keyword)
                || isCurrentTagTitleOfPostHasKeyword(post, keyword);
    }

    private boolean isCurrentTagTitleOfPostHasKeyword(Post post, String keyword) {
        for (Tag tag : post.getCurrentTag()){
            if(tag.getTitle().contains(keyword))
                return true;
        }
        return false;
    }



    @DisplayName("Post검색 - 존재하지 않는 키워드로 검색 - Service단")
    @Test
    void searchPostWithNotKeywordService() throws Exception{
        this.createPosts(13);
        String notKeyWord = "notKeyword";
        Page<Post> page0Elements = searchService.findPostByKeyword(notKeyWord, page0);

        assertEquals(0L, page0Elements.getTotalElements());

        List<Post> page0List = page0Elements.getContent();
        assertEquals(0, page0List.size());
        assertTrue(page0Elements.isEmpty());

        assertFalse(page0Elements.hasNext());
    }
}
