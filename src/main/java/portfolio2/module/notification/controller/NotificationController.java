package portfolio2.module.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.dto.NotificationDeleteRequestDto;
import portfolio2.module.notification.service.NotificationService;
import portfolio2.module.notification.validator.NotificationDeleteRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.module.main.config.UrlAndViewNameAboutMain.*;
import static portfolio2.module.main.config.VariableNameAboutMain.SESSION_ACCOUNT;
import static portfolio2.module.notification.controller.config.UrlAndViewNameAboutNotification.*;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationDeleteRequestDtoValidator notificationDeleteRequestDtoValidator;

    @InitBinder("notificationDeleteRequestDto")
    public void initBinderForNotificationDeleteRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(notificationDeleteRequestDtoValidator);
    }

    @GetMapping(ALL_NOTIFICATION_LIST_URL)
    public String showALLNotificationList(@SessionAccount Account sessionAccount,
                                          @PageableDefault(size = 3, page = 0, sort = "createdDateTime", direction = Sort.Direction.DESC)
                                                  Pageable pageable, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        Page<Notification> allNotificationPage = notificationService.ringBellCheck(sessionAccount, pageable);
        model.addAttribute("allNotificationPage", allNotificationPage);
        return ALL_NOTIFICATION_LIST_VIEW_NAME;
    }

    @GetMapping(LINK_UNVISITED_NOTIFICATION_LIST_URL)
    public String showLinkUnvisitedNotificationList(@SessionAccount Account sessionAccount,
                                                    @PageableDefault(size = 3, page = 0, sort = "createdDateTime", direction = Sort.Direction.DESC)
                                                                Pageable pageable, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        Page<Notification> linkUnvisitedNotificationPage = notificationService.getLinkUnvisitedNotification(sessionAccount, pageable);
        model.addAttribute("linkUnvisitedNotificationPage", linkUnvisitedNotificationPage);
        return LINK_UNVISITED_NOTIFICATION_LIST_VIEW_NAME;
    }

    @GetMapping(LINK_VISITED_NOTIFICATION_LIST_URL)
    public String showLinkVisitedNotificationList(@SessionAccount Account sessionAccount,
                                                  @PageableDefault(size = 3, page = 0, sort = "createdDateTime", direction = Sort.Direction.DESC)
                                                              Pageable pageable, Model model){
        model.addAttribute(SESSION_ACCOUNT, sessionAccount);
        Page<Notification> linkVisitedNotificationPage = notificationService.getLinkVisitedNotification(sessionAccount, pageable);
        model.addAttribute("linkVisitedNotificationPage", linkVisitedNotificationPage);
        return LINK_VISITED_NOTIFICATION_LIST_VIEW_NAME;
    }

    @GetMapping(NOTIFICATION_LINK_VISIT_URL + "/{notificationId}")
    public String visitLink(@SessionAccount Account sessionAccount,
                            @PathVariable("notificationId") Notification notification, Model model){
        if(!sessionAccount.getUserId().equals(notification.getAccount().getUserId())){
            model.addAttribute(SESSION_ACCOUNT, sessionAccount);
            model.addAttribute(ERROR_TITLE, "접근 오류");
            model.addAttribute(ERROR_CONTENT, "잘못된 접근입니다.");
            return ERROR_VIEW_NAME;
        }
        notificationService.linkVisitCheck(notification);
        return REDIRECT + notification.getLink();
    }

    @PostMapping(CHANGE_ALL_LINK_UNVISITED_NOTIFICATION_TO_VISITED_URL)
    public String changeAllToLinkVisited(@SessionAccount Account sessionAccount,
                                         @RequestParam String currentUrl){
        notificationService.changeAllToLinkVisited(sessionAccount);
        return REDIRECT + currentUrl;
    }

    @PostMapping(DELETE_ALL_LINK_VISITED_NOTIFICATION_URL)
    public String deleteAllLinkVisited(@SessionAccount Account sessionAccount,
                                       @RequestParam String currentUrl){
        notificationService.deleteAllLinkVisited(sessionAccount);
        return REDIRECT + currentUrl;
    }

    @ResponseBody
    @PostMapping(NOTIFICATION_DELETE_URL)
    public ResponseEntity deleteNotification(@Valid @RequestBody NotificationDeleteRequestDto notificationDeleteRequestDto,
                                             Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        notificationService.deleteNotification(notificationDeleteRequestDto);
        return ResponseEntity.ok().build();
    }
}
