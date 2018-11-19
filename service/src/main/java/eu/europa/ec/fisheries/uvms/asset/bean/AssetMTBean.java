package eu.europa.ec.fisheries.uvms.asset.bean;

import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class AssetMTBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMTBean.class);

    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    private List<IdList> createIdList(String mobtermidtype_serialnumber, String mobtermidtype_les, String mobtermidtype_dnid, String mobtermidtype_membernumber) {
        List<IdList> ret = new ArrayList<>();

        if (mobtermidtype_serialnumber != null && mobtermidtype_serialnumber.length() > 0) {
            IdList line = new IdList();
            line.setType(IdType.SERIAL_NUMBER);
            line.setValue(mobtermidtype_serialnumber);
            ret.add(line);
        }
        if (mobtermidtype_les != null && mobtermidtype_les.length() > 0) {
            IdList line = new IdList();
            line.setType(IdType.LES);
            line.setValue(mobtermidtype_les);
            ret.add(line);
        }
        if (mobtermidtype_dnid != null && mobtermidtype_dnid.length() > 0) {
            IdList line = new IdList();
            line.setType(IdType.DNID);
            line.setValue(mobtermidtype_dnid);
            ret.add(line);
        }
        if (mobtermidtype_membernumber != null && mobtermidtype_membernumber.length() > 0) {
            IdList line = new IdList();
            line.setType(IdType.MEMBER_NUMBER);
            line.setValue(mobtermidtype_membernumber);
            ret.add(line);
        }
        return ret;
    }

    private MobileTerminalType getMobileTerminalByIdList(List<IdList> ids, String rawMovementSourceName) {

        MobileTerminalSearchCriteria criteria = new MobileTerminalSearchCriteria();
        for (IdList id : ids) {
            switch (id.getType()) {
                case DNID:
                    if (id.getValue() != null) {
                        ListCriteria wrkCriteria = new ListCriteria();
                        wrkCriteria.setKey(SearchKey.DNID);
                        wrkCriteria.setValue(id.getValue());
                        criteria.getCriterias().add(wrkCriteria);
                    }
                    break;
                case MEMBER_NUMBER:
                    if (id.getValue() != null) {
                        ListCriteria wrkCriteria = new ListCriteria();
                        wrkCriteria.setKey(SearchKey.MEMBER_NUMBER);
                        wrkCriteria.setValue(id.getValue());
                        criteria.getCriterias().add(wrkCriteria);
                    }
                    break;
                case SERIAL_NUMBER:
                    if (id.getValue() != null) {
                        ListCriteria wrkCriteria = new ListCriteria();
                        wrkCriteria.setKey(SearchKey.SERIAL_NUMBER);
                        wrkCriteria.setValue(id.getValue());
                        criteria.getCriterias().add(wrkCriteria);
                    }
                    break;
                case LES:
                default:
                    LOG.error("[ERROR] Unhandled Mobile Terminal id: {} ]", id.getType());
                    break;
            }
        }

        // If no valid criterias, don't look for a mobile terminal
        if (criteria.getCriterias().isEmpty()) {
            return null;
        }

        MobileTerminalListQuery query = new MobileTerminalListQuery();

        // If we know the transponder type from the source, use it in the search criteria
        if (rawMovementSourceName != null && rawMovementSourceName.trim().length() > 0) {
            ListCriteria wrkCriteria = new ListCriteria();
            wrkCriteria.setKey(SearchKey.TRANSPONDER_TYPE);
            wrkCriteria.setValue(rawMovementSourceName);
            criteria.getCriterias().add(wrkCriteria);
        }

        query.setMobileTerminalSearchCriteria(criteria);
        ListPagination pagination = new ListPagination();
        // To leave room to find erroneous results - it must be only one in the list
        pagination.setListSize(2);
        pagination.setPage(1);
        query.setPagination(pagination);

        MobileTerminalListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(query);
        List<MobileTerminalType> resultList = mobileTerminalListResponse.getMobileTerminal();
        return resultList.size() != 1 ? null : resultList.get(0);
    }
}
