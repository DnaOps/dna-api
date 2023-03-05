package dgu.edu.dnaapi.domain.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    @ApiModelProperty(example = "DNA API 응답 코드 정보")
    private ApiStatus apiStatus;

    @ApiModelProperty(example = "응답 데이터")
    private Object data;

    public Message() {
        this.apiStatus = new ApiStatus(DnaStatusCode.BAD_REQUEST, "");
        this.data = null;
    }

    @Builder
    public Message(ApiStatus apiStatus, Object data) {
        this.apiStatus = apiStatus;
        this.data = data;
    }
}