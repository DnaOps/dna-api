package dgu.edu.dnaapi.aop;

import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
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

import static org.springframework.util.StringUtils.hasText;

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
        HttpServletRequest request = requestAttributes.getRequest();

        String token = request.getHeader("Authorization");
        if(!hasText(token))
            throw new DNACustomException(DnaStatusCode.TOKEN_INVALID);

        String userEmail = tokenProvider.getTokenSubject(token.substring(7));
        User findUser = userService.getUserByEmail(userEmail);

        if(findUser == null)
            if(!hasText(token))
                throw new DNACustomException(DnaStatusCode.TOKEN_INVALID);

        Object[] args = Arrays.stream(joinPoint.getArgs()).map(data -> {
            if(data instanceof User) {
                data = findUser;
            }
            return data;
        }).toArray();

        return joinPoint.proceed(args);
    }

}
