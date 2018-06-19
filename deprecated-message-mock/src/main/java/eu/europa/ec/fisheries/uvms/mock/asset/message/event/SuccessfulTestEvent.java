package eu.europa.ec.fisheries.uvms.mock.asset.message.event;


public class SuccessfulTestEvent {

    private String message;

    public SuccessfulTestEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
