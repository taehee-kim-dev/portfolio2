package portfolio2.account.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = SignUpAndLoggedInSecurityContextFactory.class)
public @interface SignUpAndLoggedIn {
}
