package eu.europa.ec.fisheries.uvms.asset.exception;

public class AssetSyncException extends RuntimeException {
	public AssetSyncException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
