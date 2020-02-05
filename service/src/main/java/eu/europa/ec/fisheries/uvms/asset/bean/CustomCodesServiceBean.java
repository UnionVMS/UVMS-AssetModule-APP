/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.bean;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.CustomCodeDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.List;

@Stateless
public class CustomCodesServiceBean {

    @EJB
    private CustomCodeDao dao;

    /**
     * @param constant      @description constants
     * @param code          @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate   @description code is valid to this date inclusive
     * @param description   @description human readable description of the code for dropdowns/prints etc
     * @return a CustomCode Object
     */
    public CustomCode create(String constant, String code, Instant validFromDate, Instant validToDate, String description) {
        validateParameters(constant, code, validFromDate, validToDate);

        // we allow nonvalues in description and extradata since the code can be an existent nonexistent flag
        // but we avoid nulls for simplicity
        if (description == null) {
            description = "";
        }

        if (exists(constant, code, validFromDate, validToDate)) {
            throw new IllegalArgumentException("CustomCode already exists");
        }

        CustomCode customCode = new CustomCode();
        CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code, validFromDate, validToDate);
        customCode.setPrimaryKey(primaryKey);
        customCode.setDescription(description);
        return dao.create(customCode);
    }

    public CustomCode create(CustomCode customCode) {
        if (customCode == null) {
            throw new IllegalArgumentException("CustomCode cannot be null");
        }
        return dao.create(customCode);
    }

    /**
     * @param constant      @description constants
     * @param code          @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate   @description code is valid to this date inclusive
     * @return a CustomCodes object
     */
    public CustomCode get(String constant, String code, Instant validFromDate, Instant validToDate) {
        validateParameters(constant, code, validFromDate, validToDate);
        CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code, validFromDate, validToDate);
        return dao.get(primaryKey);
    }

    public CustomCode get(CustomCodesPK primaryKey) {
        if (primaryKey == null) {
            throw new IllegalArgumentException("CustomCodesPk cannot be null");
        }
        return dao.get(primaryKey);
    }

    /**
     * @param constant      @description constants
     * @param code          @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate   @description code is valid to this date inclusive
     * @return a boolean indicating exists or not  used for validation on incoming data
     */
    public Boolean exists(String constant, String code, Instant validFromDate, Instant validToDate) {
        validateParameters(constant, code, validFromDate, validToDate);
        CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code, validFromDate, validToDate);
        return dao.exists(primaryKey);
    }

    /**
     * @param constant      @description constants
     * @param code          @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate   @description code is valid to this date inclusive
     * @param newValue      @description  new description
     * @return a the updated CustomCodes Object
     */
    public CustomCode update(String constant, String code, Instant validFromDate, Instant validToDate, String newValue) {
        validateParameters(constant, code, validFromDate, validToDate);
        CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code, validFromDate, validToDate);
        return dao.update(primaryKey, newValue);
    }

    /**
     * @param constant      @description constants
     * @param code          @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate   @description code is valid to this date inclusive
     */
    public void delete(String constant, String code, Instant validFromDate, Instant validToDate) {
        validateParameters(constant, code, validFromDate, validToDate);
        CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code, validFromDate, validToDate);
        dao.delete(primaryKey);
    }

    public List<CustomCode> getAllFor(String constant) {
        if (constant == null) {
            throw new IllegalArgumentException("Constant cannot be null");
        }
        if (constant.trim().length() < 1) {
            throw new IllegalArgumentException("Constant cannot be empty");
        }
        return dao.getAllFor(constant.toUpperCase());
    }

    public void deleteAllFor(String constant) {
        if (constant == null) {
            throw new IllegalArgumentException("Constant cannot be null");
        }
        if (constant.trim().length() < 1) {
            throw new IllegalArgumentException("Constant cannot be empty");
        }
        dao.deleteAllFor(constant.toUpperCase());
    }

    public List<String> getAllConstants() {
        return dao.getAllConstants();
    }

    public List<CustomCode> getForDate(String constant, String code, Instant aDate) {
        validateParameters(constant, code, aDate);
        return dao.getForDate(constant, code, aDate);
    }

    public Boolean verify(String constant, String code, Instant aDate) {
        validateParameters(constant, code, aDate);
        return dao.verify(constant, code, aDate);
    }

    public CustomCode replace(CustomCode customCode) {
        if (customCode == null) {
            throw new IllegalArgumentException("No CustomCode is null");
        }
        if (customCode.getPrimaryKey() == null) {
            throw new IllegalArgumentException("CustomCode primaryKey is null");
        }
        CustomCodesPK pk = customCode.getPrimaryKey();
        String constant = pk.getConstant();
        String code = pk.getCode();
        Instant validFromDate = pk.getValidFromDate();
        Instant validToDate = pk.getValidToDate();
        validateParameters(constant, code, validFromDate, validToDate);
        return dao.replace(customCode);
    }

    private void validateParameters(String constant, String code, Instant validFromDate, Instant validToDate) {
        validateParameters(constant, code, validFromDate);
        if (validToDate == null) {
            throw new IllegalArgumentException("ValidToDate cannot be null");
        }
    }

    private void validateParameters(String constant, String code, Instant aDate) {
        if (constant == null) {
            throw new IllegalArgumentException("Constant cannot be null");
        }
        if (constant.trim().length() < 1) {
            throw new IllegalArgumentException("Constant cannot be empty");
        }
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }
        if (code.trim().length() < 1) {
            throw new IllegalArgumentException("Code cannot be empty");
        }
        if (aDate == null) {
            throw new IllegalArgumentException("ValidFromDate cannot be null");
        }
    }
}
