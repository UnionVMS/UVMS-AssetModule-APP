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

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupFieldDaoBean.class);


    public AssetGroupField create(AssetGroupField field) throws AssetGroupDaoException {
        try {
            em.persist(field);
            return field;
        } catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when creating asset groupfield. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset groupfield ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when creating asset groupfield. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create assetgroupfield ] " + e.getMessage());
        }
    }

    public AssetGroupField get(Long id) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroupField> query = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_GETBYID, AssetGroupField.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting assetgroupfield by guid. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get assetgroupfield, guid: " + id + " ] " + e.getMessage());
        }
    }

    public AssetGroupField update(AssetGroupField field) throws AssetGroupDaoException {
        try {
            em.merge(field);
            return field;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when updating assetgroupfield. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ update assetgroupfield, id: " + field.toString() + " ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when updating assetgroupfield. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ update assetgroupfield ] " + e.getMessage());
        }
    }

    public AssetGroupField delete(AssetGroupField field) throws AssetGroupDaoException {
        try {
            em.remove(field);
            return field;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when deleting assetgroupfield. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ deleteassetgroupfield, id: " + field.toString() + " ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when deleting asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset ] " + e.getMessage());
        }
    }


    public List<AssetGroupField> syncFields(AssetGroupEntity assetGroup, List<AssetGroupField> assetGroupFields)  throws AssetGroupDaoException{

        try {

            Query qry = em.createNamedQuery(AssetGroupField.ASSETGROUP_FIELD_CLEAR);
            qry.setParameter("assetgroup", assetGroup);
            qry.executeUpdate();

            for(AssetGroupField assetGroupField : assetGroupFields){
                em.persist(assetGroupField);
            }
            return assetGroupFields;
        } catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when creating asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset group ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when creating asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset ] " + e.getMessage());
        }
    }




}
