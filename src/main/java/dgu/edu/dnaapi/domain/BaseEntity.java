package dgu.edu.dnaapi.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    @ApiModelProperty(example = "데이터 생성 시간")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @ApiModelProperty(example = "마지막 데이터 수정 시간")
    private LocalDateTime lastModifiedDate;
}
