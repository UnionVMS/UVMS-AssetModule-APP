package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AssetModelMapper;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

@Stateless
public class GetAssetListEventBean {

    @Inject
    MessageProducer messageProducer;

    @Inject
    AssetService service;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAssetList(AssetMessageEvent message) {
        try {
            AssetListQuery query = message.getQuery();
            List<SearchKeyValue> searchValues = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria().getCriterias());
            int page = query.getPagination().getPage();
            int listSize = query.getPagination().getListSize();
            Boolean dynamic = query.getAssetSearchCriteria().isIsDynamic();
            
            
            AssetListResponse assetList = service.getAssetList(searchValues, page, listSize, dynamic);
            
            ListAssetResponse response = AssetModelMapper.toListAssetResponse(assetList); 
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(response));
        } catch (AssetException e) {
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist: " + e.getMessage())));
        }
    }


}
