package eu.europa.ec.fisheries.uvms.mobileterminal.service.exception;

public class MobileTerminalException extends Exception {
    private static final long serialVersionUID = 3568431328513195791L;

    private final int errorCode;

    public MobileTerminalException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public MobileTerminalException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public MobileTerminalException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MobileTerminalException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
