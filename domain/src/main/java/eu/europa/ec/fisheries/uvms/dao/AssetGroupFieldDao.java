package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.uvms.entity.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Stateless
@Local
public class AssetGroupFieldDao   {


    @PersistenceContext
    private EntityManager em;


    public AssetGroupField create(AssetGroupField field) {

            em.persist(field);
            return field;
    }

    public AssetGroupField get(UUID id)  {

        try {
            TypedQuery<AssetGroupField> query = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_GETBYID, AssetGroupField.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AssetGroupField update(AssetGroupField field) {
            return em.merge(field);
    }

    public AssetGroupField delete(AssetGroupField field)  {

            em.remove(field);
            return field;
    }

    public void removeFieldsForGroup(AssetGroup assetGroup)  {

        Query qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_CLEAR);
        qry.setParameter("assetgroup", assetGroup);
        qry.executeUpdate();
    }

    public List<AssetGroupField>  retrieveFieldsForGroup(AssetGroup assetGroup)  {

        TypedQuery<AssetGroupField> qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_RETRIEVE_FIELDS_FOR_GROUP,AssetGroupField.class);
        qry.setParameter("assetgroup", assetGroup);
        List<AssetGroupField> resultSet = qry.getResultList();
        return resultSet;
    }




}
