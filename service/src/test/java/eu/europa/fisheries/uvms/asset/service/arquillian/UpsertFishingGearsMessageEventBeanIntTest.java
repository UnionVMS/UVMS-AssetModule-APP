package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.service.bean.UpsertFishingGearsMessageEventBean;
import eu.europa.ec.fisheries.uvms.mapper.EntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGearType;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.TextMessage;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

/**
 * Created by thofan on 2017-06-14.
 */


@RunWith(Arquillian.class)
public class UpsertFishingGearsMessageEventBeanIntTest extends TransactionalTests {


    @EJB
    private UpsertFishingGearsMessageEventBean upsertFishingGearsMessageEventBean;


    @Inject
    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(upsertFishingGearsMessageEventBean);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertFishingGears_nonExisting() throws Exception {

        // create
        // check

        TextMessage message = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(message);
        FishingGearDTO fishingGear = new FishingGearDTO();

        FishingGearType fishingGearType = new FishingGearType();
        fishingGearType.setCode(43L);
        fishingGearType.setName("GearType");

        fishingGear.setFishingGearType(fishingGearType);

        fishingGear.setCode("CD");
        String description = UUID.randomUUID().toString();
        fishingGear.setDescription(description);
        fishingGear.setName("Name");
        fishingGear.setExternalId(42L);

        assetMessageEvent.setFishingGear(fishingGear);
        upsertFishingGearsMessageEventBean.upsertFishingGears(assetMessageEvent);

        if (interceptorForTests.isFailed()) {
            Assert.fail();
            return;
        };

        Query query = em.createQuery("SELECT f FROM FishingGear f where externalId = :externalId and description = :description");
        query.setParameter("externalId", 42L);
        query.setParameter("description", description);

        Object  obj =  query.getSingleResult();
        Assert.assertTrue(obj!=null);
    }


    @Test
    @OperateOnDeployment("normal")
    public void upsertFishingGears_Existing() throws Exception {

        // create
        // update
        // check if update worked

        TextMessage message = null;
        AssetMessageEvent assetMessageEvent = new AssetMessageEvent(message);
        FishingGearDTO fishingGear = new FishingGearDTO();

        FishingGearType fishingGearType = new FishingGearType();
        fishingGearType.setCode(43L);
        fishingGearType.setName("GearType");

        fishingGear.setFishingGearType(fishingGearType);
        fishingGear.setCode("CD");

        String description = UUID.randomUUID().toString();
        fishingGear.setDescription(description);

        fishingGear.setName("Name");
        fishingGear.setExternalId(42L);

        assetMessageEvent.setFishingGear(fishingGear);
        upsertFishingGearsMessageEventBean.upsertFishingGears(assetMessageEvent);
        em.flush();


        TypedQuery query = em.createQuery("SELECT f FROM FishingGear f where externalId = :externalId and description = :description", eu.europa.ec.fisheries.uvms.entity.model.FishingGear.class);
        query.setParameter("externalId", 42L);
        query.setParameter("description",description);

        eu.europa.ec.fisheries.uvms.entity.model.FishingGear  tmpFetchedFishingGear = (eu.europa.ec.fisheries.uvms.entity.model.FishingGear) query.getSingleResult();
        eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO fetchedFishingGear = EntityToModelMapper.mapEntityToFishingGear(tmpFetchedFishingGear);

        description = "CHANGEDDescription";
        fetchedFishingGear.setDescription(description);
        fetchedFishingGear.setExternalId(52L);
        assetMessageEvent.setFishingGear(fetchedFishingGear);
        upsertFishingGearsMessageEventBean.upsertFishingGears(assetMessageEvent);
        em.flush();

        query = em.createQuery("SELECT f FROM FishingGear f where externalId = :externalId and description = :description", eu.europa.ec.fisheries.uvms.entity.model.FishingGear.class);
        query.setParameter("externalId", 52L);
        query.setParameter("description",description);
        eu.europa.ec.fisheries.uvms.entity.model.FishingGear  tmpFetchedFishingGear2 = (eu.europa.ec.fisheries.uvms.entity.model.FishingGear) query.getSingleResult();
        eu.europa.ec.fisheries.wsdl.asset.types.FishingGearDTO fetchedFishingGear2 = EntityToModelMapper.mapEntityToFishingGear(tmpFetchedFishingGear2);



        if (interceptorForTests.isFailed()) {
            Assert.fail();
            return;
        };


    }





}
