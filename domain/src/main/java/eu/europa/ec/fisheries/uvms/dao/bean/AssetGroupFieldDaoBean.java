package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class AssetGroupFieldDaoBean  extends Dao implements AssetGroupFieldDao {

    @Override
    public AssetGroupField create(AssetGroupField field) {

            em.persist(field);
            return field;
    }

    @Override
    public AssetGroupField get(Long id)  {

        try {
            TypedQuery<AssetGroupField> query = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_GETBYID, AssetGroupField.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public AssetGroupField update(AssetGroupField field) {

            em.merge(field);
            return field;
    }

    @Override
    public AssetGroupField delete(AssetGroupField field)  {

            em.remove(field);
            return field;
    }


    @Override
    public List<AssetGroupField> syncFields(AssetGroupEntity assetGroup, List<AssetGroupField> assetGroupFields)  {

        Query qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_CLEAR);
        qry.setParameter("assetgroup", assetGroup);
        qry.executeUpdate();

        for(AssetGroupField assetGroupField : assetGroupFields){
            em.persist(assetGroupField);
        }
        return assetGroupFields;
    }

    @Override
    public void removeFieldsForGroup(AssetGroupEntity assetGroup)  {

        Query qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_CLEAR);
        qry.setParameter("assetgroup", assetGroup);
        qry.executeUpdate();
    }

    @Override
    public List<AssetGroupField>  retrieveFieldsForGroup(AssetGroupEntity assetGroup)  {

        TypedQuery<AssetGroupField> qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_RETRIEVE_FIELDS_FOR_GROUP,AssetGroupField.class);
        qry.setParameter("assetgroup", assetGroup);
        List<AssetGroupField> resultSet = qry.getResultList();
        return resultSet;
    }




}
