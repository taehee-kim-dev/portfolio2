package portfolio2.config;

public class UrlAndViewName {

    // HomeAndLogInController.java
    public static final String REDIRECT = "redirect:";

    public static final String HOME_URL = "/";
    public static final String HOME_VIEW_NAME = "index";

    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_VIEW_NAME = "account/login";


    // SignUpController.java
    public static final String SIGN_UP_URL = "/sign-up";
    public static final String SIGN_UP_VIEW_NAME = "account/sign-up";

    public static final String EMAIL_VERIFICATION_REQUEST_VIEW_NAME = "account/email-verification-request";



    // EmailVerificationController.java
    public static final String CHECK_EMAIL_VERIFICATION_LINK_URL = "/check-email-verification-link";

    public static final String EMAIL_VERIFICATION_RESULT_VIEW_NAME = "account/email-verification-result";

    public static final String AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL = "/account/setting/account/email";
    public static final String CANNOT_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME = "account/cannot-send-email-verification-email-error";

    // ProfileController.java
    public static final String ACCOUNT_SETTING_PROFILE_URL = "/account/setting/profile";
    public static final String ACCOUNT_SETTING_PROFILE_VIEW_NAME = "account/setting/profile";

    // AccountSettingController.java
    public static final String ACCOUNT_SETTING_ACCOUNT_URL = "/account/setting/account";
    public static final String ACCOUNT_SETTING_ACCOUNT_VIEW_NAME = "account/setting/account";

    public static final String ACCOUNT_SETTING_NOTIFICATION_URL = "/account/setting/notification";
    public static final String ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME = "account/setting/notification";

    public static final String ACCOUNT_SETTING_TAG_URL = "/account/setting/tag";
    public static final String ACCOUNT_SETTING_TAG_VIEW_NAME = "account/setting/tag";

    public static final String ACCOUNT_SETTING_PASSWORD_URL = "/account/setting/password";
    public static final String ACCOUNT_SETTING_PASSWORD_VIEW_NAME = "account/setting/password";

    public static final String ACCOUNT_SETTING_ACCOUNT_EMAIL_URL = "/account/setting/account/email";
    public static final String ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL = "/account/setting/account/nickname";
}
