package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class TaskHandler extends GeneralException {
    public TaskHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
