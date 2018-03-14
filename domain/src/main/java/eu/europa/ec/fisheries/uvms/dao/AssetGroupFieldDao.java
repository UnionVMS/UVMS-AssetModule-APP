package eu.europa.ec.fisheries.uvms.dao;


import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;

import javax.ejb.Local;
import java.util.List;

@Local

public interface AssetGroupFieldDao {


    /**
     *
     * @param field
     * @return
     */
    AssetGroupField create(AssetGroupField field);

    /**
     *
     * @param id
     * @return
     */
    AssetGroupField get(Long id);

    /**
     *
     * @param field
     * @return
     */
    AssetGroupField update(AssetGroupField field);

    /**
     *
     * @param field
     * @return
     */
    AssetGroupField delete(AssetGroupField field);

    /**
     *
     * @param assetGroup
     * @param assetGroupFields
     * @return
     */
    List<AssetGroupField> syncFields(AssetGroupEntity assetGroup, List<AssetGroupField> assetGroupFields);

    /**
     *
     * @param assetGroup
     */
    void removeFieldsForGroup(AssetGroupEntity assetGroup);

    /**
     *
     * @param assetGroup
     * @return
     */
    List<AssetGroupField> retrieveFieldsForGroup(AssetGroupEntity assetGroup);

}
