package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ContactInfoDaoTest {

    @Inject
    private ContactInfoDao contactDao;

    @Test
    @OperateOnDeployment("normal")
    public void createContactInfo() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setName("Ted Gärdestad");
        contactInfo.setEmail("ted@solosommar.se");
        contactInfo.setPhoneNumber("+46734444321");

        ContactInfo returnedCI = contactDao.createContactInfo(contactInfo);
        assertNotNull(returnedCI);
    }

}
