package eu.europa.ec.fisheries.uvms.rest;

import java.util.ArrayList;
import java.util.List;

public class AppException extends RuntimeException {

    private static final long serialVersionUID = -5969484774324935334L;
    private Integer code = Integer.MIN_VALUE;

    private List<String> messages = new ArrayList<>();

    public AppException() {
        super();
    }

    public AppException(Integer applicationCode, String message, Throwable cause) {
        super("LIST", cause);
        this.code = applicationCode;
        this.messages.add(message);
    }

    public AppException(Integer applicationCode, String message) {
        super("LIST");
        this.code = applicationCode;
        this.messages.add(message);
    }

    public AppException(Integer applicationCode, List<String> messages, Throwable cause) {
        super("LIST", cause);
        this.code = applicationCode;
        this.messages.addAll(messages);
    }

    public AppException(Integer applicationCode, List<String> messages) {
        super("LIST");
        this.code = applicationCode;
        this.messages.addAll(messages);
    }

    public Integer getCode() {
        return code;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    @Override
    public String getMessage(){

        StringBuilder str = new StringBuilder();
        for(String msg : messages) {
            str.append(msg);
            str.append(",");
        }
        String ret = str.toString();
        if(messages.size() > 0) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

}