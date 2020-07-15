package portfolio2.module.account.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    private String profileImage;
}
