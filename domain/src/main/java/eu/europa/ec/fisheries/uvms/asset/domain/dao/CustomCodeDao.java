package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import javax.ejb.Stateless;
import javax.persistence.*;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import java.util.List;

@Stateless
public class CustomCodeDao {

    @PersistenceContext
    private EntityManager em;

    public CustomCode create(CustomCode record) {

        // NO DUPLICATES on constant AND code ALLOWED
        CustomCode fetched = get(record.getPrimaryKey());
        if (fetched != null) return record;
        if(record.getDescription() == null){
            record.setDescription("");
        }

        em.persist(record);
        return record;
    }


    public CustomCode get(CustomCodesPK primaryKey) {
        try {
            CustomCode customCodes = em.find(CustomCode.class, primaryKey);
            return customCodes;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public CustomCode update(CustomCodesPK primaryKey, String newDescription, String extradata) {

        CustomCode fetchedMDR_lite = get(primaryKey);
        if (fetchedMDR_lite != null) {
            if(newDescription != null) {
                fetchedMDR_lite.setDescription(newDescription);
            }
            if(extradata != null) {
                fetchedMDR_lite.setExtraData(extradata);
            }
        }
        return fetchedMDR_lite;
    }


    public void delete(CustomCodesPK primaryKey) {

        CustomCode record = get(primaryKey);
        if (record != null) {
            em.remove(record);
        }
    }


    public Boolean exists(CustomCodesPK primaryKey) {

        return get(primaryKey) != null;
    }

    public List<CustomCode> getAllFor(String constant) {
        try {
            TypedQuery<CustomCode> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETALLFOR, CustomCode.class);
            query.setParameter("constant", constant);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


    public void deleteAllFor(String constant) {

        Query query = em.createQuery("DELETE FROM CustomCode m where  m.primaryKey.constant=:constant");
        query.setParameter("constant", constant);
        query.executeUpdate();


    }

    public List<String> getAllConstants() {
        try {
            TypedQuery<String> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETALLCONSTANTS, String.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }



}
