package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.PollSearchField;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.poll.PollSearchMapper;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.hamcrest.core.StringContains;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by roblar on 2017-05-03.
 */
@RunWith(Arquillian.class)
public class PollDaoBeanIntTest extends TransactionalTests {

    @EJB
    private PollDaoBean pollDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll() {

        Poll poll = createPollHelper();
        pollDao.createPoll(poll);
        em.flush();

        assertNotNull(poll.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll_updateUserConstraintViolation() {

        thrown.expect(ConstraintViolationException.class);

        // Given
        Poll poll = createPollHelper();
        char [] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        poll.setUpdatedBy(new String(updatedBy));

        // When
        pollDao.createPoll(poll);
        em.flush();

        // Then throw Exception
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll_WithNull() {
        try {
            pollDao.createPoll(null);
            Assert.fail();

            em.flush();
        }
        catch(RuntimeException e){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll_WithDefaultGuidGeneration() {

        Poll poll = createPollHelper();
        poll.setId(null);
        pollDao.createPoll(poll);
        em.flush();

        assertNotNull(poll.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollById() {

        Poll poll = createPollHelper();
        pollDao.createPoll(poll);
        em.flush();

        Poll found = pollDao.getPollById(poll.getId());
        assertNotNull(found);
        assertEquals(poll.getId(), found.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollById_willFailWithWrongId() {

        UUID uuid = UUID.randomUUID();
        Poll poll = pollDao.getPollById(uuid);
        assertNull(poll);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollById_willFailWithNull() {

        Poll poll = pollDao.getPollById(null);
        assertNull(poll);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListByProgramPoll_willFail() {

        thrown.expect(EJBTransactionRolledbackException.class);

        Integer pollProgramId = 1;
        pollDao.getPollListByProgramPoll(pollProgramId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID() {

        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_TERMINAL_TYPE() {

        /***
         * Ok MobileTerminalTypeEnum values:
         * INMARSAT_C, IRIDIUM;
         */

        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";
        List<String> listOfPollSearchKeyValues = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_USER() {

        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        List<String> listOfPollSearchKeyValues = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue = new PollSearchKeyValue();
        pollSearchKeyValue.setSearchField(PollSearchField.USER);
        pollSearchKeyValue.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID_And_TERMINAL_TYPE() {

        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";
        List<String> listOfPollSearchKeyValues2 = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues2);


        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID_And_TERMINAL_TYPE_And_USER() {

        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> listOfPollSearchKeyValues1 = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues1);

        String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";
        List<String> listOfPollSearchKeyValues2 = Arrays.asList(testEnumValue1, testEnumValue2);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues2);

        String testValue3 = "testValue3";
        String testValue4 = "testValue4";
        List<String> listOfPollSearchKeyValues = Arrays.asList(testValue3, testValue4);

        PollSearchKeyValue pollSearchKeyValue3 = new PollSearchKeyValue();
        pollSearchKeyValue3.setSearchField(PollSearchField.USER);
        pollSearchKeyValue3.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2, pollSearchKeyValue3);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_POLL_TYPE() {

        /***
         * Ok PollTypeEnum values:
         * PROGRAM_POLL(1),
         * SAMPLING_POLL(2),
         * MANUAL_POLL(3),
         * CONFIGURATION_POLL(4);
         */
        String pollTypeEnumProgramPoll = "PROGRAM_POLL";
        String pollTypeEnumSamplingPoll = "SAMPLING_POLL";
        String pollTypeEnumManualPoll = "MANUAL_POLL";
        String pollTypeEnumConfigurationPoll = "CONFIGURATION_POLL";

        List<String> listOfPollSearchKeyValues = Arrays.asList(pollTypeEnumProgramPoll, pollTypeEnumSamplingPoll,
                pollTypeEnumManualPoll, pollTypeEnumConfigurationPoll);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_settingPollSearchField_CONNECT_ID_inPollSearchKeyValueWillBuildNoneWorkingSqlPhrase() {

        // TODO: This exception is thrown unintentionally. Should be fixed by implementing MobileTerminalConnect entity class.
        thrown.expect(EJBTransactionRolledbackException.class);
        checkExpectedMessage("could not resolve property: mobileterminalconnect of: eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal [SELECT COUNT (DISTINCT p) FROM eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Poll p  INNER JOIN p.pollBase pb INNER JOIN pb.mobileterminal mt  INNER JOIN mt.mobileterminalconnect tc  WHERE tc.connectValue IN (:connectionValue) ]");

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, true);

        pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_failOnSqlReplaceToken() {

        String sql = "SELECT COUNT (DISTINCT p) FROM Poll p ";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();

        pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_noSqlPhraseCausesException() {

        thrown.expect(EJBTransactionRolledbackException.class);
        checkExpectedMessage("unexpected end of subtree []");

        String sql = "";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_malformedSqlPhraseCausesException() {

        thrown.expect(EJBTransactionRolledbackException.class);
        checkExpectedMessage("unexpected token: * near line");

        String sql = "SELECT * FROM Poll p";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_settingPollSearchField_CONNECT_ID_inPollSearchKeyValueWillBuildNoneWorkingSqlPhrase() {

        // TODO: This exception is thrown unintentionally. Should be fixed by implementing MobileTerminalConnect entity class.
        thrown.expect(EJBTransactionRolledbackException.class);

        checkExpectedMessage("could not resolve property: mobileterminalconnect of: eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal [SELECT DISTINCT p FROM eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Poll p  INNER JOIN p.pollBase pb INNER JOIN pb.mobileterminal mt  INNER JOIN mt.mobileterminalconnect tc  WHERE tc.connectValue IN (:connectionValue) ]");

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String selectSearchSql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true);

        pollDao.getPollListSearchPaginated(pageNumber, pageSize, selectSearchSql, listOfPollSearchKeyValue);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_pageCountLessThanZeroThrowsException() {

        Integer pageNumber = 0;
        Integer pageSize = 1;

        thrown.expect(EJBTransactionRolledbackException.class);
        checkExpectedMessage("Error building query with values: Page number: " + pageNumber + " and Page size: " + pageSize);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        
        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true);

        pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValue);
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_ID() {

    	Poll poll = createPollHelper();
    	pollDao.createPoll(poll);
        em.flush();

        String testValue1 = UUID.randomUUID().toString();
        String testValue2 = UUID.randomUUID().toString();
        List<String> pollSearchKeyValueList = Arrays.asList(testValue1, testValue2);
        
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(pollSearchKeyValueList);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValue);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_TYPE() {

        /***
         * Ok PollTypeEnum values:
         * PROGRAM_POLL(1),
         * SAMPLING_POLL(2),
         * MANUAL_POLL(3),
         * CONFIGURATION_POLL(4);
         */
        String pollTypeEnumProgramPoll = "PROGRAM_POLL";
        String pollTypeEnumSamplingPoll = "SAMPLING_POLL";
        String pollTypeEnumManualPoll = "MANUAL_POLL";
        String pollTypeEnumConfigurationPoll = "CONFIGURATION_POLL";

        List<String> listOfPollSearchKeyValues = Arrays.asList(pollTypeEnumProgramPoll, pollTypeEnumSamplingPoll,
                pollTypeEnumManualPoll, pollTypeEnumConfigurationPoll);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);
        
        Integer pageNumber = 1;
        Integer pageSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValue);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_TERMINAL_TYPE() {

    	/**
         * Ok MobileTerminalTypeEnum values:
         * INMARSAT_C, IRIDIUM;
         */
    	
    	String testEnumValue1 = "INMARSAT_C";
        String testEnumValue2 = "IRIDIUM";

        List<String> pollSearchKeyValuesList = Arrays.asList(testEnumValue1, testEnumValue2);
        
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(pollSearchKeyValuesList);

        List<PollSearchKeyValue> listOfPollSearchKeyValues = Collections.singletonList(pollSearchKeyValue1);

        Integer pageNumber = 1;
        Integer pageSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValues, true);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(pageNumber, pageSize, sql, listOfPollSearchKeyValues);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_USER() {

        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        List<String> pollSearchKeyValueList = Arrays.asList(testValue1, testValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);
        pollSearchKeyValue1.setValues(pollSearchKeyValueList);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Collections.singletonList(pollSearchKeyValue1);

        Integer page = 2;
        Integer listSize = 1;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, true);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue);

        assertNotNull(pollList);
    }

    private Poll createPollHelper() {
        Poll poll = new Poll();
        poll.setUpdateTime(LocalDateTime.now());
        poll.setUpdatedBy("testUser");
        return poll;
    }

    private void checkExpectedMessage(String message) {
        thrown.expect(new ThrowableMessageMatcher(new StringContains(message)));
    }
}
