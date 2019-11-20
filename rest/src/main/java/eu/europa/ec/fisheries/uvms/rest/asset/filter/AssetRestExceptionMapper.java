package eu.europa.ec.fisheries.uvms.rest.asset.filter;

import eu.europa.ec.fisheries.uvms.rest.AppException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public class AssetRestExceptionMapper implements ExceptionMapper<Exception> {


    private static final Logger LOG = LoggerFactory.getLogger(AssetRestExceptionMapper.class);

    public AssetRestExceptionMapper() {
        super();
    }

    final class ErrorMessage {

        private Integer code;
        private List<String> appMessages = new ArrayList<>();
        private String cause = "unknown";
        private String mdc = MDC.get("requestId");

        public ErrorMessage() {
        }

        public ErrorMessage(Integer code, String appMessage, Throwable cause) {
            this.code = code;
            this.appMessages.add(appMessage);
            if (cause != null) {
                this.cause = cause.getMessage();
            }
        }

        public ErrorMessage(Integer code, List<String> appMessages, Throwable cause) {
            this.code = code;
            this.appMessages.addAll(appMessages);
            if (cause != null) {
                this.cause = cause.getMessage();
            }
        }

        public Integer getCode() {
            return code;
        }

        public List<String> getAppMsg() {
            return appMessages;
        }

        public String getCause() {
            return cause;
        }
    }

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof AppException) {
            AppException e = (AppException) exception;
            ErrorMessage errorMessage = new ErrorMessage(e.getCode(), e.getMessages(), null);
            return Response.status(200).entity(errorMessage).build();

        }

        ErrorMessage errorMessage = new ErrorMessage(500, "Internal not identified error", exception);
        return Response.status(200).entity(errorMessage).build();

    }
}
