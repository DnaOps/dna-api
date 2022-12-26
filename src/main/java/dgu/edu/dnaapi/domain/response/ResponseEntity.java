package dgu.edu.dnaapi.domain.response;

import io.jsonwebtoken.lang.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public class ResponseEntity<T> extends HttpEntity<T> {

    private final HttpStatus status;

    public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers);
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }
}
