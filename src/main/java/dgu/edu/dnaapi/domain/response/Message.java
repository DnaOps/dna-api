package dgu.edu.dnaapi.domain.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    @ApiModelProperty(example = "상태코드")
    private StatusEnum status;

    @ApiModelProperty(example = "오류 메시지")
    private String message;

    @ApiModelProperty(example = "응답 데이터")
    private Object data;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }

    @Builder
    public Message(StatusEnum status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}