package portfolio2.module.account.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;


/*
    @Data 어노테이션은 @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode을
    한꺼번에 설정해주는 매우 유용한 어노테이션
* */
@Data
@NoArgsConstructor
public class ShowPasswordUpdatePageRequestDto {

    private String email;

    private String token;
}