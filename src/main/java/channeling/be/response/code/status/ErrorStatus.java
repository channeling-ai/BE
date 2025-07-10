package channeling.be.response.code.status;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403","금지된 요청입니다."),


    //채널 관련 에러
    _CHANNEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHANNEL400","존재하지 않는 체널입니다."),
    _CHANNEL_NOT_MEMBER(HttpStatus.FORBIDDEN, "CHANNEL403", "해당 채널을 소유한 멤버가 아닙니다."),


    // 회원동의
    _MEMBER_AGREE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_AGREE400", "존재하지 않는 회원 동의입니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    //실패 응답 생성.
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    //http상태를 담은 실패 응답 생성
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
