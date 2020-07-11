package portfolio2.controller.config;

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

    public static final String EMAIL_VERIFICATION_REQUEST_VIEW_NAME = "email/email-verification-view/email-verification-request";



    // EmailVerificationController.java
    public static final String CHECK_EMAIL_VERIFICATION_LINK_URL = "/check-email-verification-link";

    public static final String EMAIL_VERIFICATION_SUCCESS_VIEW_NAME = "email/email-verification-view/email-verification-success";
    public static final String INVALID_EMAIL_LINK_ERROR_VIEW_NAME = "email/email-link-error";

    // ProfileController.java
    public static final String PROFILE_VIEW_URL = "/account/profile-view";
    public static final String PROFILE_VIEW_VIEW_NAME = "account/profile-view/profile-view";
    public static final String PROFILE_VIEW_NOT_FOUND_ERROR_VIEW_NAME = "account/profile-view/profile-view-not-found-error";

    // AccountSettingController.java
    public static final String ACCOUNT_SETTING_PROFILE_URL = "/account/setting/profile";
    public static final String ACCOUNT_SETTING_PROFILE_VIEW_NAME = "account/setting/profile-setting";

    public static final String ACCOUNT_SETTING_NOTIFICATION_URL = "/account/setting/notification";
    public static final String ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME = "account/setting/notification-setting";

    public static final String ACCOUNT_SETTING_TAG_URL = "/account/setting/tag";
    public static final String ACCOUNT_SETTING_TAG_VIEW_NAME = "account/setting/tag-setting";

    public static final String ACCOUNT_SETTING_PASSWORD_URL = "/account/setting/password";
    public static final String ACCOUNT_SETTING_PASSWORD_VIEW_NAME = "account/setting/password-setting";

    public static final String ACCOUNT_SETTING_ACCOUNT_URL = "/account/setting/account";
    public static final String ACCOUNT_SETTING_ACCOUNT_VIEW_NAME = "account/setting/account-setting";

    public static final String ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL = "/account/setting/account/nickname";
    public static final String ACCOUNT_SETTING_ACCOUNT_EMAIL_URL = "/account/setting/account/email";
    public static final String CANNOT_SEND_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME
            = "email/email-verification-view/cannot-send-email-verification-email-error";

    // CheckShowPasswordUpdatePageLinkController.java
    public static final String CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL = "/check-show-password-update-page-link";
}
