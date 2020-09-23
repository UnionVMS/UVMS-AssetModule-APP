package eu.europa.ec.fisheries.uvms.asset.service.sync.message;

public class AssetHistorySyncRetrievalMessage {

    private static final String DELIMITER = ";";

    private Integer pageNumber;
    private Integer pageSize;

    public AssetHistorySyncRetrievalMessage() {
    }

    public AssetHistorySyncRetrievalMessage(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static String encode(AssetHistorySyncRetrievalMessage message) {
        return String.join(DELIMITER, message.getPageNumber().toString(), message.getPageSize().toString());
    }

    public static AssetHistorySyncRetrievalMessage decode(String message) {
        String[] data = message.split(DELIMITER);
        return new AssetHistorySyncRetrievalMessage(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
