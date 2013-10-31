package ru.fizteh.fivt.students.belousova.multifilehashmap.tests;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileTableProvider;

import java.io.File;

public class MultiFileTableProviderTest {
    private TableProvider tableProvider;
    private File testDirectory;

    @Before
    public void setUp() throws Exception {
        testDirectory = new File("javatest");
        if (testDirectory.exists()) {
            testDirectory.delete();
        }
        testDirectory.mkdir();
        tableProvider = new MultiFileTableProvider(testDirectory);
        tableProvider.createTable("existingTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        tableProvider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() throws Exception {
        tableProvider.getTable("");
    }

    @Test
    public void testGetTableExisted() throws Exception {
        Assert.assertEquals(tableProvider.getTable("existingTable").getName(), "existingTable");
    }

    @Test
    public void testGetTableNotExisted() throws Exception {
        Assert.assertNull(tableProvider.getTable("notExistingTable"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        tableProvider.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() throws Exception {
        tableProvider.createTable("");
    }

    @Test
    public void testCreateTableExisted() throws Exception {
        Assert.assertNull(tableProvider.createTable("existingTable"));
    }

    @Test
    public void testCreateTableNotExisted() throws Exception {
        Assert.assertNotNull(tableProvider.createTable("notExistingTable"));
        tableProvider.removeTable("notExistingTable");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        tableProvider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() throws Exception {
        tableProvider.removeTable("");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExisted() throws Exception {
        tableProvider.removeTable("notExistingTable");
    }

    @Test
    public void testRemoveTableExisted() throws Exception {
        tableProvider.removeTable("existingTable");
    }
}
