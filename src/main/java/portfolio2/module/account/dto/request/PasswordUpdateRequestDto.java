package portfolio2.module.account.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PasswordUpdateRequestDto {

    private String newPassword;

    private String newPasswordConfirm;
}
