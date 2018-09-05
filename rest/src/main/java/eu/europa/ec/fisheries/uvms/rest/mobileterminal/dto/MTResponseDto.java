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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto;

import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import org.slf4j.MDC;

import java.util.Objects;

/**
 ** @param <T>
 */
public class MTResponseDto<T> {

    private final T data;
    private final Integer code;
    private String requestId = MDC.get("requestId");

    public MTResponseDto(T data, MTResponseCode code) {
        this.data = data;
        this.code = code.getCode();
    }
    
    public MTResponseDto(T data, Integer code) {
    	this.data = data;
    	this.code = code;
    }

    public T getData() {
        return data;
    }

    public Integer getCode() {
        return code;
    }

    public String getRequestId() {
        return requestId ;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.data);
        hash = 23 * hash + Objects.hashCode(this.code);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MTResponseDto<?> other = (MTResponseDto<?>) obj;
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MTResponseDto{" + "data=" + data + ", code=" + code + '}';
    }
}
