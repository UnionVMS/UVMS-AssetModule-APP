package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.uvms.entity.MDR_Lite;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
@Local
public class MDR_LiteDao {

    @PersistenceContext
    private EntityManager em;

    public MDR_Lite create(MDR_Lite record) {

        // NO DUPLICATES
        MDR_Lite fetched = get(record.getConstant(), record.getCode());
        if (fetched != null) return record;

        em.persist(record);
        return record;
    }

    public MDR_Lite get(String constant, String value) {
        try {
            TypedQuery<MDR_Lite> query = em.createNamedQuery(MDR_Lite.MDRLITE_GET, MDR_Lite.class);
            query.setParameter("constant", constant);
            query.setParameter("code", value);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Boolean exists(String constant, String value) {
            TypedQuery<MDR_Lite> query = em.createNamedQuery(MDR_Lite.MDRLITE_GET, MDR_Lite.class);
            query.setParameter("constant", constant);
            query.setParameter("code", value);
            return query.getResultList().size() == 1;
    }

    public List<MDR_Lite> getAllFor(String constant) {
        try {
            TypedQuery<MDR_Lite> query = em.createNamedQuery(MDR_Lite.MDRLITE_GETALLFOR, MDR_Lite.class);
            query.setParameter("constant", constant);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


    public void delete(String constant, String value) {

        MDR_Lite record = get(constant, value);
        if (record != null) {
            em.remove(record);
        }
    }

    public void deleteAllFor(String constant) {

        Query query = em.createQuery("DELETE FROM MDR_Lite m where  m.constant=:constant");
        query.setParameter("constant", constant);
        query.executeUpdate();


    }


}
