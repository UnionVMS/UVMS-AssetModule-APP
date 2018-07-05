package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.*;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ChannelDaoIntTest extends TransactionalTests {

    @Inject
    private ChannelDaoBean channelDao;

    @EJB
    private TestPollHelper testPollHelper;

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch() {
        //Given - need a string list of id's.
        String id1 = "test_id1";
        String id2 = "test_id2";
        List<String> idList = Arrays.asList(id1, id2);

        MobileTerminal mobileTerminal = testPollHelper.createMobileTerminal(id1);
        assertNotNull(mobileTerminal.getId());

        //When
        List<Channel> channels = channelDao.getPollableListSearch(idList);

        //Then
        assertNotNull(channels);
        assertEquals(1, channels.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_emptyList() {

        //Given - empty id list
        List<String> emptyList = new ArrayList<>();

        //When
        List<Channel> channels = channelDao.getPollableListSearch(emptyList);

        //Then
        assertNotNull(channels);
        assertThat(channels.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_NULL() {

        //Given - null
        List<String> nullAsList = null;

        //When
        List<Channel> channels = channelDao.getPollableListSearch(nullAsList);

        //Then
        assertNotNull(channels);
        assertThat(channels.size(), is(0));
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    @Ignore //rewrite to not use channelHistory
    public void testGetActiveDNID() {

        //Given
        String pluginName = "test_getActiveDNID";

        //When
        List<String> dnidList = channelDao.getActiveDNID(pluginName);

        //Then
        assertNotNull(dnidList);
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    @Ignore //rewrite to not use channelHistory
    public void testGetActiveDNID_emptyList() {

        //Given
        String pluginName = null;

        //When
        List<String> dnidList = channelDao.getActiveDNID(pluginName);

        //Then
        assertThat(dnidList.size(), is(0));
    }
}
