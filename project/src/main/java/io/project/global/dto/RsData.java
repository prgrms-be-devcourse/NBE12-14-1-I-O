package io.project.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "공통 응답 규격")
public class RsData<T> {

    @Schema(description = "응답 상태 코드")
    private String resultCode;

    @Schema(description = "응답 안내 메시지")
    private String msg;

    @Schema(description = "실제 반환 데이터 (데이터가 없을 경우 null)")
    private T data;

    public RsData(String resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = null;
    }





}
