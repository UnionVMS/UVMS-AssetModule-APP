package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
@Local
public class AssetGroupFieldDaoBean  extends Dao {

    public AssetGroupField create(AssetGroupField field) {

            em.persist(field);
            return field;
    }

    public AssetGroupField get(Long id)  {

        try {
            TypedQuery<AssetGroupField> query = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_GETBYID, AssetGroupField.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetGroupField update(AssetGroupField field) {

            em.merge(field);
            return field;
    }

    public AssetGroupField delete(AssetGroupField field)  {

            em.remove(field);
            return field;
    }


    // convinience method . . . .
    public List<AssetGroupField> syncFields(AssetGroupEntity assetGroup, List<AssetGroupField> assetGroupFields)  {

        Query qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_CLEAR);
        qry.setParameter("assetgroup", assetGroup);
        qry.executeUpdate();

        for(AssetGroupField assetGroupField : assetGroupFields){
            em.persist(assetGroupField);
        }
        return assetGroupFields;
    }

    public void removeFieldsForGroup(AssetGroupEntity assetGroup)  {

        Query qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_CLEAR);
        qry.setParameter("assetgroup", assetGroup);
        qry.executeUpdate();
    }




}
