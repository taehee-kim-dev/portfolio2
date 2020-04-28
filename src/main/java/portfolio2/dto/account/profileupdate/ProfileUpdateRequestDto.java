package portfolio2.dto.account.profileupdate;

import lombok.Data;

@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    private String profileImage;
}
