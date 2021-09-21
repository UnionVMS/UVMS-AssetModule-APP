package eu.europa.ec.fisheries.uvms.asset.remote.dto;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChangeHistoryTest {

    @Test
    public void changeHistoryRowTest () {
        ChangeHistoryRow changeHistoryRow = new ChangeHistoryRow("updated", Instant.now());
        assertEquals("updated", changeHistoryRow.getUpdatedBy());
        assertNotNull(changeHistoryRow.getUpdateTime());
    }


    @Test
    public void changeHistoryItemTest () {
        ChangeHistoryItem changeHistoryItem = new ChangeHistoryItem("field", "oldvalue", "newvalue");
        assertEquals("field", changeHistoryItem.getField());
        assertEquals("oldvalue", changeHistoryItem.getOldValue());
        assertEquals("newvalue", changeHistoryItem.getNewValue());
    }

}
