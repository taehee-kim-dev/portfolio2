package portfolio2.module.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.CustomPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.REDIRECT;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationRepository notificationRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(modelAndView != null && !isRedirectView(modelAndView) && authentication != null && authentication.getPrincipal() instanceof CustomPrincipal){
            Account sessionAccount = ((CustomPrincipal)(authentication.getPrincipal())).getSessionAccount();
            Long count = notificationRepository.countByAccountAndChecked(sessionAccount, false);
            modelAndView.addObject("notificationCount", count);
        }

    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith(REDIRECT) || modelAndView.getView() instanceof RedirectView;
    }
}
