package portfolio2.dto.request.account.setting.update;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PasswordUpdateRequestDto {

    private String newPassword;

    private String newPasswordConfirm;
}
