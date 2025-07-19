package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class S3Handler extends GeneralException {
    public S3Handler(BaseErrorCode code) {
        super(code);
    }
}
