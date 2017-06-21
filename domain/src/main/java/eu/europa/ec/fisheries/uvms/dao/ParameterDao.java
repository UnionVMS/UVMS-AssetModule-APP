package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.dao.exception.ConfigServiceException;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by thofan on 2017-06-21.
 */

@Local
public interface ParameterDao {


    /**
     * Returns the parameter value as a string.
     *
     * @param key a parameter key
     * @return a string representation of the parameter value
     * @throws ConfigServiceException if unsuccessful
     */
    public String getStringValue(String key) throws ConfigServiceException;

    /**
     * Remove the parameter by key
     * @param key a parameter key
     * @return true if able to remove, false otherwise
     * @throws ConfigServiceException
     */
    public boolean removeParameter(String key) throws ConfigServiceException;

    /**
     * @param keys a list of parameter keys
     * @return a list of all settings matching one of the keys
     * @throws ConfigServiceException if unsuccessful
     */
    public List<SettingType> getSettings(List<String> keys) throws ConfigServiceException;

    /**
     * Get all settings in parameter table
     * @return
     * @throws ConfigServiceException
     */
    public List<SettingType> getAllSettings() throws ConfigServiceException;

    /**
     * Sets a value for the specified key.
     *
     * @param key a parameter key
     * @param value a value
     * @param description a description of the parameter
     * @throws ConfigServiceException if unsuccessful
     */
    public boolean setStringValue(String key, String value, String description) throws ConfigServiceException;

    /**
     * Returns the parameter value as a boolean.
     * Persisted value must be case insensitively "true" or "false".
     *
     * @param key a parameter key
     * @return a boolean representation of the parameter value
     * @throws ConfigServiceException if parameter is neither "true" nor "false"
     */
    public Boolean getBooleanValue(String key) throws ConfigServiceException;

    /**
     * Removes any parameter with the specified key.
     *
     * @param key a parameter key
     * @throws ConfigServiceException if unsuccessful
     */
    public void reset(String key) throws ConfigServiceException;

    /**
     * Removes all parameters.
     *
     * @throws ConfigServiceException if unsuccessful
     */
    public void clearAll() throws ConfigServiceException;

}
