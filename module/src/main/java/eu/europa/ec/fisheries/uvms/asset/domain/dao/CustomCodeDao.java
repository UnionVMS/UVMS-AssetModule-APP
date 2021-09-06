package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.time.Instant;
import java.util.List;

@Stateless
public class CustomCodeDao {

    @PersistenceContext
    private EntityManager em;

    public CustomCode create(CustomCode record) {
        em.persist(record);
        return record;
    }

    public CustomCode get(CustomCodesPK primaryKey) {
        try {
            return em.find(CustomCode.class, primaryKey);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public CustomCode update(CustomCodesPK primaryKey, String newDescription) {

        CustomCode customCode = get(primaryKey);
        if (customCode != null) {
            if (newDescription != null) {
                customCode.setDescription(newDescription);
            }
        }
        return customCode;
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
        } catch (NotFoundException e) {
            return false;
        }
    }

    public List<CustomCode> getAllFor(String constant) {
        TypedQuery<CustomCode> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETALLFOR, CustomCode.class);
        query.setParameter("constant", constant);
        return query.getResultList();
    }

    public void deleteAllFor(String constant) {

        List<CustomCode> rs = getAllFor(constant);
        if (rs != null) {
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

    public List<CustomCode> getForDate(String constant, String code, Instant aDate) {

        TypedQuery<CustomCode> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE, CustomCode.class);
        query.setParameter("constant", constant);
        query.setParameter("code", code);
        query.setParameter("aDate", aDate);
        List<CustomCode> customCodes = query.getResultList();
        return customCodes;
    }

    public Boolean verify(String constant, String code, Instant aDate) {
        TypedQuery<CustomCode> query = em.createNamedQuery(CustomCode.CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE, CustomCode.class);
        query.setParameter("constant", constant);
        query.setParameter("code", code);
        query.setParameter("aDate", aDate);
        List<CustomCode> customCodes = query.getResultList();
        return customCodes.size() > 0;
    }

    // delets old and adds new
    public CustomCode replace(CustomCode customCode) {
        CustomCodesPK primaryKey = customCode.getPrimaryKey();
        if (exists(primaryKey)) {
            delete(primaryKey);
        }
        CustomCode createdCustomCode = em.merge(customCode);
        return createdCustomCode;
    }
}
