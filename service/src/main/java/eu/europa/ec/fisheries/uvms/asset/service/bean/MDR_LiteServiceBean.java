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

import eu.europa.ec.fisheries.uvms.asset.service.MDR_LiteService;
import eu.europa.ec.fisheries.uvms.dao.MDR_LiteDao;
import eu.europa.ec.fisheries.uvms.entity.MDR_Lite;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;


@Stateless
public class MDR_LiteServiceBean implements MDR_LiteService {

	@EJB
	private MDR_LiteDao dao;


	@Override
	public MDR_Lite create(String constant, String code, String description, String extradata){

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
		// we allow nonvalues in description and extradata since the code can be a existent nonexistent flag
		// but we avoid nulls for simplicity
		if(description == null){
			description = "";
		}
		if(extradata == null){
			extradata = "";
		}
		MDR_Lite mdr = new MDR_Lite();
		mdr.setConstant(constant);
		mdr.setCode(code);
		mdr.setDescription(description);
		mdr.setJsonstr(extradata);

		return dao.create(mdr);
	}


	@Override
	public MDR_Lite get(String constant, String code ){

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
		return dao.get(constant,code);

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

		return dao.exists(constant,code);

	}

	@Override
	public MDR_Lite update(String constant, String code, String newValue, String newExtraData){

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
		return dao.update(constant,code,newValue,newExtraData);

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
		dao.delete(constant,code);

	}

	@Override
	public List<MDR_Lite> getAllFor(String constant){
		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		return dao.getAllFor(constant);

	}

	@Override
	public void deleteAllFor(String constant){
		if(constant == null){
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if(constant.trim().length() < 1){
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		dao.deleteAllFor(constant);

	}
}