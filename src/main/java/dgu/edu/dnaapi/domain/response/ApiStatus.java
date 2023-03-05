package dgu.edu.dnaapi.domain.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiStatus {

    @ApiModelProperty(example = "DNA 응답 상태코드")
    private DnaStatusCode errorCode;

    @ApiModelProperty(example = "오류 메시지")
    private String errorMessage;

    public ApiStatus(DnaStatusCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
