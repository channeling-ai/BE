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

    //서버로직도중 토큰 유실
    _GOOGLE_ACCESS_TOKEN_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON501", "구글 액세스 토큰이 존재하지 않습니다."),

    //유튜브 관련 에러
    _YOUTUBE_PLAYLIST_PULLING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "YOUTUBE500", "유튜브 플레이리스트를 가져오는 중 에러가 발생했습니다."),


    //채널 관련 에러
    _CHANNEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHANNEL400","존재하지 않는 체널입니다."),
    _CHANNEL_NOT_MEMBER(HttpStatus.FORBIDDEN, "CHANNEL403", "해당 채널을 소유한 멤버가 아닙니다."),

    // 회원동의
    _MEMBER_AGREE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_AGREE400", "존재하지 않는 회원 동의입니다."),
    _MEMBER_AGREE_NOT_ALLOW(HttpStatus.BAD_REQUEST, "MEMBER_AGREE400", "로그인 된 멤버의 동의 정보만 수정 가능합니다."),

    //멤버 관련 에러
    _MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER400", "존재하지 않는 멤버입니다."),
    _SNS_LINK_INVALID(HttpStatus.BAD_REQUEST, "MEMBER401", "SNS 링크가 유효하지 않습니다."),

    //S3 관련 에러
    _FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "S3400", "파일 업로드에 실패했습니다."),
    _FILE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "S3401", "파일 삭제에 실패했습니다."),

    //아이디어 관련 에러
    _IDEA_NOT_FOUND(HttpStatus.BAD_REQUEST, "IDEA400", "존재하지 않는 아이디어입니다."),
    _IDEA_NOT_MEMBER(HttpStatus.BAD_REQUEST, "IDEA403", "해당 아이디어를 소유한 멤버가 아닙니다."),

    ;


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
