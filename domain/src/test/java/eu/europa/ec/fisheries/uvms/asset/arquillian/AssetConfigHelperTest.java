package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.bean.property.ParameterKey;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetConfigHelperTest implements ConfigHelper {

    private final static String ASSET_PU = "asset";

    @PersistenceContext
    protected EntityManager em;

    @Override
    public List<String> getAllParameterKeys() {
        List<String> keys = new ArrayList<String>();
        for (ParameterKey parameterKey : ParameterKey.values()) {
            keys.add(parameterKey.getKey());
        }

        return keys;
    }

    @Override
    public String getModuleName() {
        return ASSET_PU;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
