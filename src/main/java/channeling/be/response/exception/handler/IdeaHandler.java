package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class IdeaHandler extends GeneralException {
    public IdeaHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
