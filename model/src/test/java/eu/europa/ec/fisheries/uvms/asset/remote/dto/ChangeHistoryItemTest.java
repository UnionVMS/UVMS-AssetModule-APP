package eu.europa.ec.fisheries.uvms.asset.remote.dto;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChangeHistoryItemTest {

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

    @Test
    public void changeHistoryItemSetterTest () {
        ChangeHistoryItem item = new ChangeHistoryItem();

        assertNull(item.getField());
        assertNull(item.getOldValue());
        assertNull(item.getNewValue());

        item.setField("fieldTest");
        item.setOldValue("oldValueTest");
        item.setNewValue("NewValueTest");

        assertEquals("fieldTest", item.getField());
        assertEquals("oldValueTest", item.getOldValue());
        assertEquals("NewValueTest", item.getNewValue());
    }

}
