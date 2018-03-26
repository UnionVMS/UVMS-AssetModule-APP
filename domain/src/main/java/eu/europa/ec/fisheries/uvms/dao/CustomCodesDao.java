package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.uvms.entity.CustomCodes;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
@Local
public class CustomCodesDao {

    @PersistenceContext
    private EntityManager em;

    public CustomCodes create(CustomCodes record) {

        // NO DUPLICATES on constant AND code ALLOWED
        CustomCodes fetched = get(record.getConstant(), record.getCode());
        if (fetched != null) return record;
        if(record.getDescription() == null){
            record.setDescription("");
        }
        if(record.getJsonstr() == null){
            record.setJsonstr("");
        }

        em.persist(record);
        return record;
    }

    public CustomCodes get(String constant, String code) {
        try {
            TypedQuery<CustomCodes> query = em.createNamedQuery(CustomCodes.CUSTOMCODES_GET, CustomCodes.class);
            query.setParameter("constant", constant);
            query.setParameter("code", code);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomCodes update(String constant, String code, String newDescription, String newJson) {

        CustomCodes fetchedMDR_lite = get(constant, code);
        if (fetchedMDR_lite != null) {
            if(newDescription != null) {
                fetchedMDR_lite.setDescription(newDescription);
            }
            if(newJson != null) {
                fetchedMDR_lite.setJsonstr(newJson);
            }
        }
        return fetchedMDR_lite;
    }


    public void delete(String constant, String code) {

        CustomCodes record = get(constant, code);
        if (record != null) {
            em.remove(record);
        }
    }


    public Boolean exists(String constant, String code) {
        TypedQuery<CustomCodes> query = em.createNamedQuery(CustomCodes.CUSTOMCODES_GET, CustomCodes.class);
        query.setParameter("constant", constant);
        query.setParameter("code", code);
        return query.getResultList().size() == 1;
    }

    public List<CustomCodes> getAllFor(String constant) {
        try {
            TypedQuery<CustomCodes> query = em.createNamedQuery(CustomCodes.CUSTOMCODES_GETALLFOR, CustomCodes.class);
            query.setParameter("constant", constant);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


    public void deleteAllFor(String constant) {

        Query query = em.createQuery("DELETE FROM CustomCodes m where  m.constant=:constant");
        query.setParameter("constant", constant);
        query.executeUpdate();


    }


}
