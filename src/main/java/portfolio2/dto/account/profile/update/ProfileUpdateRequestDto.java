package portfolio2.dto.account.profile.update;

import lombok.Data;

@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    private String profileImage;
}
