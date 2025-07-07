package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class ChannelHandler extends GeneralException {
    public ChannelHandler(BaseErrorCode errorCode) {
        super(errorCode);

    }
}
