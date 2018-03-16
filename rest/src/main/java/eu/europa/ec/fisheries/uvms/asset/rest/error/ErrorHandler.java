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
package eu.europa.ec.fisheries.uvms.asset.rest.error;

import eu.europa.ec.fisheries.uvms.asset.types.AssetFault;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.exception.UnauthorizedException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetFaultException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ErrorHandler {
    final static Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    public static ResponseDto getFault(Exception ex) {
        if (ex instanceof AssetServiceException) {
            if (ex instanceof InputArgumentException) {
                return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.INPUT_ERROR);
            }

            return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.SERVICE_ERROR);
        }

        if (ex instanceof AssetModelException) {
            if (ex instanceof InputArgumentException) {
                return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.INPUT_ERROR);
            }

            if (ex instanceof AssetModelMapperException) {
                return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.MAPPING_ERROR);
            }

            if (ex instanceof AssetFaultException) {
                return extractFault((AssetFaultException) ex);
            }

            return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.MODEL_ERROR);
        }

        if (ex instanceof AssetException) {
            return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.ASSET_ERROR);
        }

        if (ex instanceof RuntimeException) {
            if (ex.getCause() instanceof UnauthorizedException) {
                return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.UNAUTHORIZED);
            }
        }
        LOG.error(ex.getMessage());
        return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.UNDEFINED_ERROR);
    }

    private static ResponseDto<String> extractFault(AssetFaultException ex) {
        String fault = ex.getMessage();
        if (fault != null) {
            return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.MAPPING_ERROR);
        }
        return new ResponseDto<String>(ex.getMessage(), ResponseCodeConstant.DOMAIN_ERROR);
    }

}