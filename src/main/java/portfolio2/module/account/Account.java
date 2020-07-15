package portfolio2.module.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import portfolio2.module.tag.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/*
    @EqualsAndHashCode(of = "id") :
    연관관계가 복잡해질 때,
    서로 다른 연관관계를 순환참조를 하느라 무한루프가 발생하고,
    스택오버플로우가 발생할 수 있다.
* */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String nickname;

    private String nicknameBeforeUpdate;



    @Column(unique = true)
    private String verifiedEmail;

    @Column(unique = true)
    private String emailWaitingToBeVerified;



    private String password;



    private boolean isEmailFirstVerified = false;

    private boolean isEmailVerified = false;



    private String emailVerificationToken;

    private LocalDateTime firstCountOfSendingEmailVerificationEmailSetAt;

    private int countOfSendingEmailVerificationEmail = 0;


    private String showPasswordUpdatePageToken;


    /*
        @Lob :
        기본적으로 String은 varchr(255)로 매핑됨.
        그것보다 더 커질 것 같을 때 @Lob으로 매핑을 해주면
        texttype으로 매핑을 해줌.

        @Basic(fetch = FetchType.EAGER) :
        부모 엔티티를 조회하면 자식 엔티티도 다같이 조회된다.

        @Basic(fetch = FetchType.LAZY) :
        부모 엔티티를 조회하면 자식 엔티티는 조회하지 않는다.
        대신, 조회한 부모 엔티티의 child 변수에 프록시 객체를 넣어둔다.
        이 프록시 객체는 실제 사용 될 때까지는 데이터베이스를 조회하지 않고,
        데이터가 필요한 순간이 되어서야 데이터베이스를 조회해서 프록시 객체를 초기화한다.

        기본값은 LAZY이나, 기본값 설정이 바뀔 수 있기 때문에 명시하는것이 좋음.
        유저를 로딩할 때 종종 같이 쓰일 것 같아서.
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private String bio;

    private String occupation;

    private String location;



    private LocalDateTime joinedAt;




    private boolean notificationLikeOnMyPostByWeb = true;

    private boolean notificationLikeOnMyReplyByWeb = true;


    private boolean notificationReplyOnMyPostByWeb = true;

    private boolean notificationReplyOnMyReplyByWeb = true;


    private boolean notificationNewPostWithMyTagByWeb = true;



    private boolean notificationLikeOnMyPostByEmail = false;

    private boolean notificationLikeOnMyReplyByEmail = false;


    private boolean notificationReplyOnMyPostByEmail = false;

    private boolean notificationReplyOnMyReplyByEmail = false;


    private boolean notificationNewPostWithMyTagByEmail = false;


    @ManyToMany
    private Set<Tag> interestTag = new HashSet<>();

    public boolean canSendEmailVerificationEmail() {
        // 인증 이메일을 이미 5번 보냈는가?
        if(this.countOfSendingEmailVerificationEmail == 5){
            // 보냈다면, 1번째 보냈을 때 보다 현재 12시간이 지났는가?
            return this.firstCountOfSendingEmailVerificationEmailSetAt.isBefore(LocalDateTime.now().minusHours(12));
        }
        return true;
    }

    public void generateEmailCheckToken() {
        // 앞에서 재전송 허가받은 상태이므로,
        // 무조건 토큰값 생성.
        this.emailVerificationToken = UUID.randomUUID().toString();
    }

    public void increaseOrResetCountOfSendingEmailVerificationEmail() {
        if(countOfSendingEmailVerificationEmail == 0 || countOfSendingEmailVerificationEmail == 5){
            // 이미 앞에서 이메일 재전송 허가받은 상태.
            // 현재 회원가입 후 첫번째 또는 12시간 후 6번째 이메일 전송이라면,
            // 새로운 첫 번째 이메일 전송이 되는 것이므로,
            // 첫 번째 카운트 세팅 시간 새로 설정
            firstCountOfSendingEmailVerificationEmailSetAt = LocalDateTime.now();
            // 이메일 전송 카운트 1로 초기화
            countOfSendingEmailVerificationEmail = 1;
        }else{
            // 회원가입 후 첫 번째나, 12시간 후 6번째 전송이 아니라면
            // 첫 번째 카운트 세팅 시간 새로 설정 없이,
            // 이메일 전송 카운트 횟수만 증가
            this.countOfSendingEmailVerificationEmail++;
        }
    }


    public void generateShowPasswordUpdatePageToken() {
        this.showPasswordUpdatePageToken = UUID.randomUUID().toString();
    }
}
