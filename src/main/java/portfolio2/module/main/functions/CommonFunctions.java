package portfolio2.module.main.functions;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class CommonFunctions {

    public void addPagingAttributes(Model model, Page<?> page, String pageName, String sortPropertyName) {
        model.addAttribute(pageName, page);
        int currentPageRangeFirstIndex = page.getNumber() / 5 * 5;
        model.addAttribute("currentPageRangeFirstIndex", currentPageRangeFirstIndex);
        int currentPageFullRangeLastIndex = currentPageRangeFirstIndex + 4;
        int currentPageRangeLastIndex = Math.min(page.getTotalPages() - 1, currentPageFullRangeLastIndex);
        model.addAttribute("currentPageRangeLastIndex", currentPageRangeLastIndex);
        model.addAttribute("sortProperty", sortPropertyName);
    }
}
