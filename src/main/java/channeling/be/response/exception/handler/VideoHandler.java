package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class VideoHandler extends GeneralException {
    public VideoHandler(BaseErrorCode code) {
        super(code);
    }
}
