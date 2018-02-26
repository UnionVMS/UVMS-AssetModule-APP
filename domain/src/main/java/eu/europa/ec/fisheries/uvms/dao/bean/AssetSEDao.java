package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class AssetSEDao {

    @PersistenceContext
    EntityManager em;


    public AssetSE createAsset(AssetSE asset) {
        em.persist(asset);
        return asset;
    }

    public AssetSE find(UUID id) {
        return em.find(AssetSE.class, id);
    }
}
