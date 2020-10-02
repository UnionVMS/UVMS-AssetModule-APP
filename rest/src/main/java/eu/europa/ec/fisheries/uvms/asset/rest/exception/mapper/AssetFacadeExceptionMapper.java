package eu.europa.ec.fisheries.uvms.asset.rest.exception.mapper;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import eu.europa.ec.fisheries.uvms.asset.rest.dto.AssetErrorResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.exception.AssetFacadeException;

@Provider
public class AssetFacadeExceptionMapper implements ExceptionMapper<AssetFacadeException> {

    @Override
    public Response toResponse(AssetFacadeException exception) {
        AssetErrorResponseDto errorDto = new AssetErrorResponseDto(exception.getError().getCode(), exception.getError().getCodeName(), exception.getMessage());
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(errorDto).build();
    }

}

