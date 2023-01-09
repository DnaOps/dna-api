package dgu.edu.dnaapi.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticesDeleteRequestDto {
    private Long id;

    @Builder
    public NoticesDeleteRequestDto(Long id) {
        this.id = id;
    }
}
