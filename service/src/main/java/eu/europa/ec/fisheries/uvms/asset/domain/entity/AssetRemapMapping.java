package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class AssetRemapMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    UUID oldAssetId;
    UUID newAssetId;
    Instant createdDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOldAssetId() {
        return oldAssetId;
    }

    public void setOldAssetId(UUID oldAssetId) {
        this.oldAssetId = oldAssetId;
    }

    public UUID getNewAssetId() {
        return newAssetId;
    }

    public void setNewAssetId(UUID newAssetId) {
        this.newAssetId = newAssetId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
