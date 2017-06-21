package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.entity.Parameter;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.ParameterDao;
import eu.europa.ec.fisheries.uvms.dao.exception.ConfigServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thofan on 2017-06-21.
 */

@Stateless
public class ParameterDaoBean  extends Dao implements ParameterDao {

    final static Logger LOG = LoggerFactory.getLogger(ParameterDaoBean.class);


    @Override
    public String getStringValue(String key) throws ConfigServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.FIND_BY_ID, Parameter.class);
            query.setParameter("id", key);
            return query.getSingleResult().getParamValue();
        }
        catch (Exception e) {
            LOG.error("[ Error when getting String value ] {}", e.getMessage());
            throw new ConfigServiceException("[ Error when getting String value. ]");
        }
    }

    @Override
    public boolean removeParameter(String key) throws ConfigServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.FIND_BY_ID, Parameter.class);
            query.setParameter("id", key);
            Parameter parameter = query.getSingleResult();
            em.remove(parameter);
            em.flush();
            return true;
        } catch (Exception e) {
            LOG.error("[ Error when remove parameter " + key + " ]");
            throw new ConfigServiceException("[ Error when remove parameter " + key + " ]");
        }
    }

    public List<SettingType> getSettings(List<String> keys) throws ConfigServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.LIST_ALL_BY_IDS, Parameter.class);
            query.setParameter("ids", keys);

            List<SettingType> settings = new ArrayList<>();
            for (Parameter parameter : query.getResultList()) {
                SettingType setting = new SettingType();
                setting.setKey(parameter.getParamId());
                setting.setValue(parameter.getParamValue());
                setting.setDescription(parameter.getParamDescription());
                settings.add(setting);
            }

            return settings;
        }
        catch (Exception e) {
            LOG.error("[ Error when getting settings by IDs. ] {}", e.getMessage());
            throw new ConfigServiceException("[ Error when getting settings by IDs. ]");
        }
    }

    @Override
    public List<SettingType> getAllSettings() throws ConfigServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.LIST_ALL, Parameter.class);

            List<SettingType> settings = new ArrayList<>();
            for (Parameter parameter : query.getResultList()) {
                SettingType setting = new SettingType();
                setting.setKey(parameter.getParamId());
                setting.setValue(parameter.getParamValue());
                setting.setDescription(parameter.getParamDescription());
                settings.add(setting);
            }

            return settings;
        }
        catch (Exception e) {
            LOG.error("[ Error when getting all settings. ] {}", e.getMessage());
            throw new ConfigServiceException("[ Error when getting all settings. ]");
        }
    }

    @Override
    public boolean setStringValue(String key, String value, String description) throws ConfigServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.FIND_BY_ID, Parameter.class);
            query.setParameter("id", key);
            List<Parameter> parameters = query.getResultList();

            if (parameters.size() == 1) {
                // Update existing parameter
                parameters.get(0).setParamValue(value);
                em.flush();
            }
            else {
                if (!parameters.isEmpty()) {
                    // Remove all parameters occurring more than once
                    for (Parameter parameter : parameters) {
                        em.remove(parameter);
                    }
                }

                // Create new parameter
                Parameter parameter = new Parameter();
                parameter.setParamId(key);
                parameter.setParamDescription(description != null ? description : "-");
                parameter.setParamValue(value);
                em.persist(parameter);
            }
            return true;
        }
        catch (Exception e) {
            LOG.error("[ Error when setting String value. ] {}={}, {}, {}", key, value, description, e.getMessage());
            throw new ConfigServiceException("[ Error when setting String value. ]");
        }
    }

    @Override
    public Boolean getBooleanValue(String key) throws ConfigServiceException {
        try {
            return parseBooleanValue(getStringValue(key));
        }
        catch (Exception e) {
            LOG.error("[ Error when getting Boolean value. ]", e.getMessage());
            throw new ConfigServiceException("[ Error when getting Boolean value. ]");
        }
    }

    @Override
    public void reset(String key) throws ConfigServiceException {
        List<Parameter> parameters;
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.FIND_BY_ID, Parameter.class);
            query.setParameter("id", key);
            parameters = query.getResultList();
        }
        catch (Exception e) {
            LOG.error("[ Error when removing parameters. ]", e.getMessage());
            throw new ConfigServiceException(e.getMessage());
        }

        for (Parameter parameter : parameters) {
            try {
                em.remove(parameter);
            }
            catch (Exception e) {
                LOG.error("[ Error when removing parameter. ]", e.getMessage());
            }
        }
    }

    @Override
    public void clearAll() throws ConfigServiceException {
        List<Parameter> parameters;
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(Parameter.LIST_ALL, Parameter.class);
            parameters = query.getResultList();
        }
        catch (Exception e) {
            LOG.error("[ Error when clearing all settings. ] {}", e.getMessage());
            throw new ConfigServiceException("[ Error when clearing all settings. ]");
        }

        for (Parameter parameter : parameters) {
            try {
                em.remove(parameter);
            }
            catch (Exception e) {
                LOG.error("[ Error when removing parameter. ]", e.getMessage());
            }
        }
    }

    private Boolean parseBooleanValue(String value) throws InputArgumentException {
        if (value.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        else if (value.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        else {
            LOG.error("[ Error when parsing Boolean value from String, The String provided dows not equal 'TRUE' or 'FALSE'. The value is {} ]", value);
            throw new InputArgumentException("The String value provided does not equal boolean value, value provided = " + value);
        }
    }



}
