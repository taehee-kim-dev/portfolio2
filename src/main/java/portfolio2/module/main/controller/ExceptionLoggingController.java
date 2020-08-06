package portfolio2.module.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import portfolio2.module.account.Account;
import portfolio2.module.account.config.SessionAccount;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionLoggingController {

    @ExceptionHandler
    public String handleRuntimeException(@SessionAccount Account sessionAccount,
                                         HttpServletRequest httpServletRequest,
                                         Exception exception){

        if (sessionAccount != null){
            log.info("'{}' requested '{}'", sessionAccount.getUserId(), httpServletRequest.getRequestURI());
        }else{
            log.info("requested '{}'", httpServletRequest.getRequestURI());
        }
        log.error("bad request", exception);

        return "error";
    }
}
