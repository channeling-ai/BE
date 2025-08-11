package channeling.be.response.exception.handler;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;

public class ReportHandler extends GeneralException {
    public ReportHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}