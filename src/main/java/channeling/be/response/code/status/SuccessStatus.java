package channeling.be.response.code.status;

import channeling.be.response.code.BaseCode;
import channeling.be.response.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    //성공상태 ENUM 값
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    //채널 관련 성공상태
    _GET_CHANNEL_VIDEOS_OK(HttpStatus.OK, "CHANNEL200", "채널 비디오 조회 성공"),

    //멤버 관련 성공상태
    _MEMBER_UPDATE_SNS_OK(HttpStatus.OK, "MEMBER200", "멤버 SNS 정보 수정 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    //성공 응답 생성.
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    //http상태를 담은 성공 응답 생성
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
