package ua.com.juja.sqlcmd.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class DatabaseManagerTest {
    private DatabaseManager manager;

    protected abstract DatabaseManager getDatabaseManager();

    @Before
    public void setup() {
        manager = getDatabaseManager();
        manager.connect("sqlcmd_db", "postgres", "777");
    }

    @Test
    public void testGetAllTableName() {
        Set<String> tableNames = manager.getTableNames();
        assertEquals("[test, user_info]", tableNames.toString());
    }

    @Test
    public void testGetTableData() {
        //given
        manager.clear("user_info");

        //when
        DataSet input = new DataSetImpl();
        input.put("id", 13);
        input.put("name", "Stiven Pupkin");
        input.put("password", "password");
        manager.insert("user_info", input);

        //then
        List<DataSet> users = manager.getTableData("user_info");
        assertEquals(1, users.size());

        DataSet user = users.get(0);
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[13, Stiven Pupkin, password]", user.getValues().toString());
    }

    @Test
    public void testUpdateTableData() {
        //given
        manager.clear("user_info");
        DataSet input = new DataSetImpl();
        input.put("id", 13);
        input.put("name", "Stiven Pupkin");
        input.put("password", "password");
        manager.insert("user_info", input);

        //when
        DataSet newValue = new DataSetImpl();
        newValue.put("password", "pass2");

        DataSet checkData = new DataSetImpl();
        checkData.put("id", 13);
        manager.update("user_info", checkData, newValue);

        //then
        List<DataSet> users = manager.getTableData("user_info");
        assertEquals(1, users.size());

        DataSet user = users.get(0);
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[13, Stiven Pupkin, pass2]", user.getValues().toString());
    }

    @Test
    public void testDeleteEntryFromTable() {
        //given
        manager.clear("user_info");
        DataSet input = new DataSetImpl();
        input.put("id", 13);
        input.put("name", "Stiven Pupkin");
        input.put("password", "password");
        manager.insert("user_info", input);

        input.put("id", 14);
        input.put("name", "Bob Jons");
        input.put("password", "anaconda");
        manager.insert("user_info", input);

        List<DataSet> users = manager.getTableData("user_info");
        assertEquals(2, users.size());

        DataSet user1 = users.get(0);
        assertEquals("[id, name, password]", user1.getNames().toString());
        assertEquals("[13, Stiven Pupkin, password]", user1.getValues().toString());

        DataSet user2 = users.get(1);
        assertEquals("[id, name, password]", user2.getNames().toString());
        assertEquals("[14, Bob Jons, anaconda]", user2.getValues().toString());

        //when
        DataSet deleteData = new DataSetImpl();
        deleteData.put("name", "Bob Jons");

        //then
        manager.delete("user_info", deleteData);
        users = manager.getTableData("user_info");
        assertEquals(1, users.size());
    }

    @Test
    public void testCreateTable() {
        //given
        List<String> input = Arrays.asList("name", "password");
        String tableName = "some_table";

        //when
        manager.create(tableName, input);

        //then
        Set<String> tableNames = manager.getTableNames();
        assertEquals("[test, user_info, some_table]", tableNames.toString());

        Set<String> tableColumns = manager.getTableColumns(tableName);
        assertEquals("[id, name, password]", tableColumns.toString());

        //delete table after create
        manager.drop(tableName);
    }

    @Test
    public void testGetTableColumns() {
        //given
        String tableName = "user_info";
        manager.clear(tableName);

        //when
        Set<String> tableColumns = manager.getTableColumns(tableName);

        //then
        assertEquals("[id, name, password]", tableColumns.toString());
    }

    @Test
    public void testIsConnected() {
        assertTrue(manager.isConnected());
    }

    @Test
    public void testClearTableName() {
        //given
        manager.clear("user_info");
        DataSet input = new DataSetImpl();
        input.put("id", 13);
        input.put("name", "Stiven Pupkin");
        input.put("password", "password");

        manager.insert("user_info", input);
        List<DataSet> users = manager.getTableData("user_info");

        assertEquals(1, users.size());
        DataSet user = users.get(0);
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[13, Stiven Pupkin, password]", user.getValues().toString());

        //then
        manager.clear("user_info");
        List<DataSet> dataAfterClear = manager.getTableData("user_info");

        //when
        assertEquals(0, dataAfterClear.size());
    }
}
