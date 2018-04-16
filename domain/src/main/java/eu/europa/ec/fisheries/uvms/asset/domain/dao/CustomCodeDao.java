package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.ws.rs.NotFoundException;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    public CustomCode update(CustomCodesPK primaryKey, String newDescription, Map<String,String> nameValue) {

        CustomCode fetchedMDR_lite = get(primaryKey);
        if (fetchedMDR_lite != null) {
            if(newDescription != null) {
                fetchedMDR_lite.setDescription(newDescription);
            }
        }
        fetchedMDR_lite.setNameValue(nameValue);
        return fetchedMDR_lite;
    }


    public void delete(CustomCodesPK primaryKey) {

        CustomCode record = get(primaryKey);
        if (record != null) {
            em.remove(record);
        }
    }


    public Boolean exists(CustomCodesPK primaryKey) {

        try {
            CustomCode customCode = get(primaryKey);
            return customCode != null;
        }
        catch(NotFoundException e){
            return false;
        }
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

        List<CustomCode> rs = getAllFor(constant);
        if(rs != null) {
            for (CustomCode customCode : rs) {
                em.remove(customCode);
            }
        }
    }

    public List<String> getAllConstants() {
        try {
            TypedQuery<String> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETALLCONSTANTS, String.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


    public CustomCode getForDate(String constant, String code, LocalDateTime aDate) {

        try {
            TypedQuery<CustomCode> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE, CustomCode.class);
            query.setParameter("constant", constant);
            query.setParameter("code", code);
            query.setParameter("aDate", aDate);
            CustomCode  customCode = query.getSingleResult();
            return customCode;
        } catch (NoResultException e) {
            return null;
        }




    }
}
