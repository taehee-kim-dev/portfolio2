package portfolio2.module.notification.dto.response;

import lombok.Data;

@Data
public class EachNotificationCountResponseDto {
    long totalNotificationCount;
    long linkUnvisitedNotificationCount;
    long linkVisitedNotificationCount;
}
