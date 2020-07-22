package portfolio2.module.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;
import portfolio2.module.account.dto.request.TagUpdateRequestDto;
import portfolio2.module.notification.dto.NotificationDeleteRequestDto;
import portfolio2.module.notification.service.NotificationService;
import portfolio2.module.notification.validator.NotificationDeleteRequestDtoValidator;

import javax.validation.Valid;

import static portfolio2.module.notification.config.UrlAndViewNameAboutNotification.NOTIFICATION_DELETE_URL;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationDeleteRequestDtoValidator notificationDeleteRequestDtoValidator;

    @InitBinder("notificationDeleteRequestDto")
    public void initBinderForNotificationDeleteRequestDtoValidator(WebDataBinder webDataBinder){
        webDataBinder.addValidators(notificationDeleteRequestDtoValidator);
    }

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
