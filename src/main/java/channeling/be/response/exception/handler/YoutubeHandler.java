package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class YoutubeHandler extends GeneralException {
    public YoutubeHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
