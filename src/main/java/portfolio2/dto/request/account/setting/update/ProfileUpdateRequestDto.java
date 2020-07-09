package portfolio2.dto.request.account.setting.update;

import lombok.Data;

@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    private String profileImage;
}
