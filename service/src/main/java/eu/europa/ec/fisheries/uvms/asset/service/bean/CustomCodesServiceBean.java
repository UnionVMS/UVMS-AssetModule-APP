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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.CustomCodeDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.asset.service.CustomCodesService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;


@Stateless
public class CustomCodesServiceBean implements CustomCodesService {

	@EJB
	private CustomCodeDao dao;


	@Override
	public CustomCode create(String constant, String code, String description, Map<String,String> nameValue){

		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if(code == null){
			throw new IllegalArgumentException("Code cannot be null");
		}
		if(code.trim().length() < 1){
			throw new IllegalArgumentException("Code cannot be empty");
		}
		// we allow nonvalues in description and extradata since the code can be an existent nonexistent flag
		// but we avoid nulls for simplicity
		if(description == null){
			description = "";
		}
		CustomCode mdr = new CustomCode();
		CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code);
		mdr.setPrimaryKey(primaryKey);
		mdr.setDescription(description);
		if(nameValue != null && nameValue.size() > 0){
			mdr.setNameValue(nameValue);
		}
		return dao.create(mdr);
	}


	@Override
	public CustomCode get(String constant, String code ){

		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if(code == null){
			throw new IllegalArgumentException("Code cannot be null");
		}
		if(code.trim().length() < 1){
			throw new IllegalArgumentException("Code cannot be empty");
		}

		CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code);
		return dao.get(primaryKey);

	}

	@Override
	public Boolean exists(String constant, String code){

		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if(code == null){
			throw new IllegalArgumentException("Code cannot be null");
		}
		if(code.trim().length() < 1){
			throw new IllegalArgumentException("Code cannot be empty");
		}

		CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code);
		return dao.exists(primaryKey);

	}

	@Override
	public CustomCode update(String constant, String code, String newValue, Map<String,String> nameValue){

		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if(code == null){
			throw new IllegalArgumentException("Code cannot be null");
		}
		if(code.trim().length() < 1){
			throw new IllegalArgumentException("Code cannot be empty");
		}

		CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code);

		return dao.update(primaryKey, newValue, nameValue);

	}

	@Override
	public void delete(String constant, String code){
		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if(code == null){
			throw new IllegalArgumentException("Code cannot be null");
		}
		if(code.trim().length() < 1){
			throw new IllegalArgumentException("Code cannot be empty");
		}
		CustomCodesPK primaryKey = new CustomCodesPK(constant.toUpperCase(), code);
		dao.delete(primaryKey);

	}

	@Override
	public List<CustomCode> getAllFor(String constant){
		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		return dao.getAllFor(constant.toUpperCase());

	}

	@Override
	public void deleteAllFor(String constant){
		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		dao.deleteAllFor(constant.toUpperCase());

	}

	@Override
	public List<String> getAllConstants(){

		return dao.getAllConstants();

	}



}