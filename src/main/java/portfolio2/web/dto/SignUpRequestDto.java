package portfolio2.web.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


/*
    @Data 어노테이션은 @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode을
    한꺼번에 설정해주는 매우 유용한 어노테이션
* */
@Data
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    @Length(min = 5, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{5,20}$")
    private String userId;

    @NotBlank
    @Length(min = 3, max = 15)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{3,15}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 30)
    private String password;

    @Builder
    public SignUpRequestDto(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}