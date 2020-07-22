package portfolio2.module.notification.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.CustomPrincipal;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.notification.dto.NotificationDeleteRequestDto;

@RequiredArgsConstructor
@Component
public class NotificationDeleteRequestDtoValidator implements Validator {

    private final NotificationRepository notificationRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NotificationDeleteRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NotificationDeleteRequestDto notificationDeleteRequestDto = (NotificationDeleteRequestDto)target;

        Notification notificationToDelete = notificationRepository.findById(notificationDeleteRequestDto.getNotificationIdToDelete()).orElse(null);
        if(notificationToDelete == null){
            errors.reject("notFound");
        }else if(!notificationToDelete.getAccount().getUserId().equals(this.getSessionAccount().getUserId())){
            errors.reject("notOwner");
        }
    }

    private Account getSessionAccount(){
        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customPrincipal.getSessionAccount();
    }
}
