package dgu.edu.dnaapi.aop;

import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.service.UserService;
import dgu.edu.dnaapi.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class JwtUserAspect {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    @Around("execution(* *(.., @dgu.edu.dnaapi.annotation.JwtRequired (*), ..))")
    public Object getUserByToken(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request =requestAttributes.getRequest();

        String token = request.getHeader("Authorization");
        String userEmail = tokenProvider.getTokenSubject(token.substring(7));
        User user = userService.getUserByEmail(userEmail);

        Object[] args = Arrays.stream(joinPoint.getArgs()).map(data -> {
            if(data instanceof User) {
                data = user;
            }
            return data;
        }).toArray();

        return joinPoint.proceed(args);

    }

}
