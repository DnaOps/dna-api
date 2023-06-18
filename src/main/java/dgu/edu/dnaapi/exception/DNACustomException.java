package dgu.edu.dnaapi.exception;

import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import lombok.Getter;

@Getter
public class DNACustomException extends RuntimeException {

    private DnaStatusCode errorCode;

    public DNACustomException(String errorMessage, DnaStatusCode errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public DNACustomException(DnaStatusCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
