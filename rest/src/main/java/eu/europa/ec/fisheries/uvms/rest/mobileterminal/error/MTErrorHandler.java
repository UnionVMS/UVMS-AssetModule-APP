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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.error;

import eu.europa.ec.fisheries.uvms.mobileterminal.exception.*;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTResponseDto;

public class MTErrorHandler {
    
    public static MTResponseDto getFault(Exception ex) {

    	/*if(ex instanceof MobileTerminalModelException) {
//    		if(ex instanceof MobileTerminalModelValidationException) {
//    			return new MTResponseDto<>(ex.getMessage(), MTResponseCode.INPUT_ERROR);
//    		}
//
//    		if(ex instanceof MobileTerminalModelMapperException) {
//    			//MobileTerminalValidationException
//        		//MobileTerminalUnmarshallException
//    			return new MTResponseDto<>(ex.getMessage(), MTResponseCode.MAPPING_ERROR);
//    		}
//
//    		if(ex instanceof MobileTerminalFaultException) {
//        		return extractFault((MobileTerminalFaultException)ex);
//        	}
    		
    		return new MTResponseDto<>(ex.getMessage(), MTResponseCode.MODEL_ERROR);
    	}*/

    	/*if(ex instanceof MobileTerminalModelException) {
    		return new MTResponseDto<>(ex.getMessage(), MTResponseCode.MOBILE_TERMINAL_ERROR);
    	}*/
        return new MTResponseDto<>(ex.getMessage(), MTResponseCode.UNDEFINED_ERROR);
    }

//    private static MTResponseDto extractFault(MobileTerminalFaultException ex) {
//        MobileTerminalFault fault = ex.getMobileTerminalFault();
//        if (fault == null) {
//            return new MTResponseDto<>(ex.getMessage(), MTResponseCode.DOMAIN_ERROR);
//        }
//
//        MobileTerminalType terminal = fault.getTerminal();
//        if (terminal == null) {
//            return new MTResponseDto<>(fault.getMessage(), fault.getCode());
//        }
//        return new MTResponseDto<>(terminal, fault.getCode());
//    }
}
