package portfolio2.module.search.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.search.service.SearchService;
import portfolio2.module.post.Post;

import static portfolio2.module.main.config.VariableNameAboutMain.SESSION_ACCOUNT;
import static portfolio2.module.search.controller.config.UrlAndViewNameAboutSearch.SEARCH_POST_RESULT_VIEW_NAME;
import static portfolio2.module.search.controller.config.UrlAndViewNameAboutSearch.SEARCH_POST_URL;

@RequiredArgsConstructor
@Controller
public class SearchController {

    private final SearchService searchService;

    @GetMapping(SEARCH_POST_URL)
    public String searchPost(@SessionAccount Account sessionAccount, String keyword,
                             @PageableDefault(size = 15, page = 0, sort = "firstWrittenDateTime", direction = Sort.Direction.DESC)
                                     Pageable pageable, Model model){
        keyword = keyword.trim();
        Page<Post> postPage = searchService.findPostByKeyword(keyword, pageable);
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        model.addAttribute("keyword", keyword);
        model.addAttribute("postPage", postPage);
        int currentPageRangeFirstIndex = postPage.getNumber() / 5 * 5;
        model.addAttribute("currentPageRangeFirstIndex", currentPageRangeFirstIndex);
        int currentPageFullRangeLastIndex = currentPageRangeFirstIndex + 4;
        int currentPageRangeLastIndex = Math.min(postPage.getTotalPages() - 1, currentPageFullRangeLastIndex);
        System.out.println("***");
        System.out.println("currentPageRangeFirstIndex : " + currentPageRangeFirstIndex);
        System.out.println("currentPageFullRangeLastIndex : " + currentPageFullRangeLastIndex);
        System.out.println("currentPageRangeLastIndex : " + currentPageRangeLastIndex);
        model.addAttribute("currentPageRangeLastIndex", currentPageRangeLastIndex);
        model.addAttribute("sortProperty", "firstWrittenDateTime");
        return SEARCH_POST_RESULT_VIEW_NAME;
    }
}

