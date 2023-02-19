package com.bdreiss.zentodo;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.bdreiss.zentodo.dataManipulation.Data;
import com.bdreiss.zentodo.dataManipulation.Entry;
import com.bdreiss.zentodo.dataManipulation.database.DbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class DataTest {

    private static Context appContext;

    private static final String DATABASE_NAME = "TEST.db";

    @Before
    public void setup(){
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    //Test if the Data object is instantiated correctly
    @Test
    public void constructor(){
        Data data = new Data(appContext, DATABASE_NAME);

        DbHelper db = new DbHelper(appContext, DATABASE_NAME);

        assert(db.loadEntries().size() == 0);
        assert(db.loadLists().size() == 0);
        assert(data.getEntries().size() == 0);

        appContext.deleteDatabase(DATABASE_NAME);
    }

    //Tests if tasks are added correctly
    @Test
    public void add(){

        //Test data that contains all commonly used special characters and in particular "'", since
        //there have been problems adding tasks containing "'", have been causing problems for
        //SQL commands. Should be fixed now but better safe than sorry.
        String[] stringData = DbHelper_V1Test.stringTestData;

        Data data = new Data(appContext, DATABASE_NAME);

        for (int i = 0; i < stringData.length; i++){

            data.add(stringData[i]);

            //get entries of Data instance and assert results
            Entry entry = data.getEntries().get(i);

            assert(entry.getTask().equals(stringData[i]));
            assert(entry.getId() == i);
            assert(entry.getPosition() == i);

            //get new instance with data from the save file and assert results
            DbHelper db = new DbHelper(appContext, DATABASE_NAME);
            entry = db.loadEntries().get(i);

            assert(entry.getTask().equals(stringData[i]));
            assert(entry.getId() == i);
            assert(entry.getPosition() == i);

        }


        appContext.deleteDatabase(DATABASE_NAME);
    }

    //test if ids are generated correctly. The id will always be the lowest number still not used
    //by a task starting from 0, i.e. the first task will be 0, the second 1 and so on. If the
    //first task is removed however, the third one will be 0 again, but the fourth will be 2.
    @Test
    public void idGeneration(){

        //strings representing ids for creating test tasks
        String[] strings = {"0","1","2","3"};

        //instantiate TestClass
        TestClass test = new TestClass(appContext, strings);
        test.set();

        Data data = test.getData();

        //assert ids match task
        for (Entry e: data.getEntries())
            assert(e.getId() == Integer.parseInt(e.getTask()));

        //remove second task with expected result: "0": 0, "2": 2, "3": 3}
        data.remove(1);

        //add task with expected result: {"0": 0, "2": 2, "3": 3, "4": 1}
        data.add("4");

        //assert that added task has expected id
        for (Entry e : data.getEntries())
            assert !e.getTask().equals("4") || (e.getId() == 1);

        //remove third task with expected result: {"0": 0, "3": 3, "4": 1}
        data.remove(2);

        //add task with expected result: {"0": 0, "3": 3, "4": 1, "5": 2}
        data.add("5");

        //assert that task has expected id
        for (Entry e : data.getEntries())
            assert !e.getTask().equals("5") || (e.getId() == 2);

        appContext.deleteDatabase(DATABASE_NAME);
    }

    //tests the removal of tasks from entryStrings via their ids
    //for each consecutive test the tasks are reset, so all tests are run on the same data
    //besides tasks being removed, also the position of other tasks might change
    @Test
    public void remove(){

        //ids to be removed
        int[][] tests = {{},{0}, {1}, {2},{0,1},{1,2},{0,2},{0,1,2}};

        //expected entries after removal
        int[][] results = {{0,1,2},{1,2},{0,2},{0,1},{2},{0},{1},{},};

        //expected positions of tasks after removal
        int[][] resultsPosition = {{0,1,2}, {0,1}, {0,1}, {0,1}, {0},{0},{0},{}};

        //dummy tasks
        String[] entryStrings = {"0","1","2"};

        TestClass test = new TestClass(appContext, entryStrings);

        for (int i = 0; i < tests.length; i++){

            test.set();

            Data data = test.getData();

            for (int j = 0; j < tests[i].length; j++)
                data.remove(tests[i][j]);

            for (int j = 0; j < data.getEntries().size(); j++) {
                assert (data.getEntries().get(j).getId() == results[i][j]);
                assert (data.getEntries().get(j).getPosition() == resultsPosition[i][j]);
            }
        }

        appContext.deleteDatabase(DATABASE_NAME);
    }

    //tests swapping tasks
    @Test
    public void swap(){

        //ids being swapped
        int[][][] tests = {{{0,1}}, {{1,2}}, {{0,2}}, {{1,0}}, {{2,1}}, {{2,0}},
                            {{0,1},{1,0}}, {{0,2},{1,2}},{{0,1},{1,2}}
                            };

        //expected results in entries
        int[][] results = {{1,0,2}, {0,2,1}, {2,1,0}, {1,0,2}, {0,2,1}, {2,1,0},
                            {0,1,2}, {1,2,0}, {2,0,1}
                            };

        //dummy test data
        String[] entryStrings = {"0","1","2"};

        TestClass test = new TestClass(appContext, entryStrings);

        for (int i = 0; i < tests.length; i++){

            test.set();

            Data data = test.getData();

            for (int j = 0; j < tests[i].length; j++)
                data.swap(tests[i][j][0],tests[i][j][1]);

            for (int j = 0; j < data.getEntries().size(); j++)
                assert(data.getEntries().get(j).getId() == results[i][j]);

        }

        appContext.deleteDatabase(DATABASE_NAME);

    }


    //tests swapping items in lists: when items in a list are swapped, not only the Entry.listPosition
    //but also Entry.position
    @Test
    public void swapList(){

        //dummy test data
        String[] stringData = {"0","1","2","3","4","5"};

        //lists assigned to tasks in stringData
        String[] tests = {"0", "1", "0", "1", "0", "1"};

        //ids being swapped
        int[][][] swaps = {{{0,2}},{{0,4}}, {{2,4}},{{2,0}},{{4,0}}, {{4,2}},
                {{0,2},{2,4}},{{0,2},{2,4},{0,4}},{{0,2},{2,4},{0,4},{2,4}}
        };

        //expected results in list (which is generated dynamically)
        int[][] resultsListPosition = {
                {1,0,0,1,2,2},
                {2,0,1,1,0,2},
                {0,0,2,1,1,2},
                {1,0,0,1,2,2},
                {2,0,1,1,0,2},
                {0,0,2,1,1,2},
                {1,0,2,1,0,2},
                {0,0,2,1,1,2},
                {0,0,1,1,2,2}
        };

        //expected results in Data.entries
        int[][] resultsPosition = {
                {2,1,0,3,4,5},
                {4,1,2,3,0,5},
                {0,1,4,3,2,5},
                {2,1,0,3,4,5},
                {4,1,2,3,0,5},
                {0,1,4,3,2,5},
                {2,1,4,3,0,5},
                {0,1,4,3,2,5},
                {0,1,2,3,4,5}

        };

        TestClass test = new TestClass(appContext, stringData, tests);

        for (int i = 0; i < swaps.length; i++){
            test.set();

            Data data = test.getData();

            for (int j = 0; j < swaps[i].length; j++)
                data.swapList(swaps[i][j][0],swaps[i][j][1]);

            //assert results
            for (Entry e : data.getEntries()) {
                assert (e.getListPosition() == resultsListPosition[i][e.getId()]);
                assert (e.getPosition() == resultsPosition[i][e.getId()]);
            }
        }


    }

    //tests if the position of tasks in Data.entries is returned correctly
    @Test
    public void getPosition(){

        //dummy test data
        String[] taskStrings = {"0","1","2","3"};

        TestClass test = new TestClass(appContext, taskStrings);

        test.set();

        //assert results
        for (int i = 0; i < taskStrings.length; i++)
            assert(test.getData().getPosition(i)==i);

    }

    //tests if task is set properly
    @Test
    public void setTask(){

        //dummy test data
        String[] stringData = {"0","1","2"};

        TestClass test = new TestClass(appContext, stringData);
        test.set();
        Data data = test.getData();

        //set every task to "TEST"
        for (int i = 0; i < stringData.length; i++){
            data.setTask(i, "TEST");
        }

        //assert that every task has been set to "TEST"
        for (Entry e : data.getEntries())
            assert(e.getTask().equals("TEST"));

        //assert that all changes have been made persistent
        for (Entry e : new DbHelper(appContext, DATABASE_NAME).loadEntries())
            assert(e.getTask().equals("TEST"));

    }

    //test the function to set Entry.focus
    //first sets all tasks focus to true and asserts, than to false and asserts
    @Test
    public void setFocus(){

        //dummy tasks
        String[] stringData = {"0","1","2"};

        TestClass test = new TestClass(appContext, stringData);
        test.set();
        Data data = test.getData();

        //set all tasks focus to true
        for (int i = 0; i < stringData.length; i++){
            data.setFocus(i, true);
        }

        //assert results
        for (Entry e : data.getEntries())
            assert(e.getFocus());

        //assert results are persistent
        for (Entry e : new DbHelper(appContext, DATABASE_NAME).loadEntries())
            assert(e.getFocus());

        //set all tasks focus to false
        for (int i = 0; i < stringData.length; i++){
            data.setFocus(i, false);
        }

        //assert results
        for (Entry e : data.getEntries())
            assert(!e.getFocus());

        //assert results are persistent
        for (Entry e : new DbHelper(appContext, DATABASE_NAME).loadEntries())
            assert(!e.getFocus());
    }

    @Test
    public void setDropped(){

        String[] stringData = {"0","1","2"};

        TestClass test = new TestClass(appContext, stringData);

        test.set();

        Data data = test.getData();

        for (int i = 0; i < stringData.length; i++){
            data.setDropped(i, true);
        }

        for (Entry e : data.getEntries())
            assert(e.getDropped());

        for (Entry e : new DbHelper(appContext, DATABASE_NAME).loadEntries())
            assert(e.getDropped());


        for (int i = 0; i < stringData.length; i++){
            data.setDropped(i, false);
        }

        for (Entry e : data.getEntries())
            assert(!e.getDropped());

        for (Entry e : new DbHelper(appContext, DATABASE_NAME).loadEntries())
            assert(!e.getDropped());
    }

    @Test
    public void editReminderDate(){

        String[] taskStrings = {"0","1","2","3"};

        int[] tests = {0, 20110311, 0, 2000};

        TestClass test = new TestClass(appContext, taskStrings);

        test.set();

        Data data = test.getData();

        for (int i = 0; i < taskStrings.length; i++)
            data.editReminderDate(i,tests[i]);

        ArrayList<Entry> entries = new DbHelper(appContext, DATABASE_NAME).loadEntries();

        for (int i = 0; i < taskStrings.length; i++)
            assert(entries.get(0).getReminderDate()== tests[entries.get(0).getId()]);

        appContext.deleteDatabase(DATABASE_NAME);
    }

    @Test
    public void editRecurrence(){

        String[] taskStrings = {"0","1","2","3"};

        String[] tests = {"w2", "d999", "y88", "m3"};

        TestClass test = new TestClass(appContext, taskStrings);

        test.set();

        Data data = test.getData();

        for (int i = 0; i < taskStrings.length; i++)
            data.editRecurrence(i,tests[i]);

        ArrayList<Entry> entries = new DbHelper(appContext, DATABASE_NAME).loadEntries();

        for (int i = 0; i < taskStrings.length; i++)
            assert(entries.get(0).getRecurrence().equals(tests[entries.get(0).getId()]));

        for (int i = 0; i < tests.length; i++)
            data.editRecurrence(i,null);

        entries = new DbHelper(appContext, DATABASE_NAME).loadEntries();

        for (Entry e : entries)
            assert(e.getRecurrence()==null);

        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void editList(){

        String[] stringData = {"0","1","2","3","4","5"};

        String[] tests = {"0", "1", "0", "1", "0", "1"};

        String[] resultsList = {"0", "1", "0", "1", "0", "1"};

        int[] resultsPosition = {0,0,1,1,2,2};

        TestClass test = new TestClass(appContext, stringData, tests);

        test.set();

        Data data = test.getData();

        ArrayList<Entry> entries = new DbHelper(appContext,DATABASE_NAME).loadEntries();
        for (int i = 0; i < entries.size(); i++) {
            assert(entries.get(i).getList().equals(resultsList[i]));
            assert(entries.get(i).getListPosition() == resultsPosition[i]);
        }

        for (String s : tests) {
            assert (data.getLists().contains(s));
            assert(new DbHelper(appContext,DATABASE_NAME).loadLists().containsKey(s));
        }

        data.editList(0,null);

        assert(data.getEntries().get(2).getListPosition() == 0);
        assert(data.getEntries().get(4).getListPosition() == 1);

        data.editList(2,null);

        assert(data.getEntries().get(4).getListPosition() == 0);

        data.editList(4,null);

        assert(data.getLists().size() == 3);

        appContext.deleteDatabase(DATABASE_NAME);
    }

    @Test
    public void editListColor(){

        DbHelper db = new DbHelper(appContext,DATABASE_NAME);

        String[] lists = {"0", "1", "2"};
        String[] initialColors = {"WHITE", "RED", "BLUE"};
        String[] results = {"BLUE", "WHITE", "RED"};
        for (int i = 0; i < lists.length; i++)
            db.addList(lists[i],initialColors[i]);

        Data data = new Data(appContext,DATABASE_NAME);

        for (int i = 0; i < lists.length;i++)
            data.editListColor(lists[i],results[i]);

        for (int i = 0; i < lists.length; i++)
            assert(data.getListColor(lists[i]).equals(results[i]));

        appContext.deleteDatabase(DATABASE_NAME);


    }

    @Test
    public void getListColor(){

        DbHelper db = new DbHelper(appContext,DATABASE_NAME);

        String[] lists = {"0", "1", "2"};
        String[] colors = {"WHITE", "RED", "BLUE"};

        for (int i = 0; i < lists.length; i++)
            db.addList(lists[i],colors[i]);

        Data data = new Data(appContext,DATABASE_NAME);

        for (int i = 0; i < lists.length; i++)
            assert(data.getListColor(lists[i]).equals(colors[i]));

        appContext.deleteDatabase(DATABASE_NAME);

    }

    //TODO test for incrementListPositionCount

    @Test
    public void decrementListPositionCount(){
        String[] tasks = {"0","1","2", "3", "4", "5"};
        String[] lists = {"0","0", "0", "1", "1", "1"};


        String[][][] tests = {{{"2",null}},{{"1",null}},{{"0", null}},
                {{"2", null},{"1",null}},{{"1", null},{"0",null}},{{"2", null},{"0",null}},
                {{"1", null},{"2",null}},{{"0", null},{"1",null}},{{"0", null},{"2",null}},
                {{"2", null},{"1",null},{"0",null}}, {{"1", null},{"2",null},{"0",null}}, {{"0", null},{"1",null},{"2",null}}
        };

        int[][] results = {{0, 1, -1, 0, 1, 2},{0, -1, 1, 0, 1, 2},{-1, 0, 1, 0, 1, 2},
                {0, -1, -1, 0, 1, 2},{-1, -1, 0, 0, 1, 2},{-1, 0, -1, 0, 1, 2},
                {0, -1, -1, 0, 1, 2},{-1, -1, 0, 0, 1, 2},{-1, 0, -1, 0, 1, 2},
                {-1, -1, -1, 0, 1, 2},{-1, -1, -1, 0, 1, 2},{-1, -1, -1, 0, 1, 2}

        };

        TestClass test = new TestClass(appContext, tasks,lists);

        for (int i = 0; i < tests.length; i++){

            test.set();

            Data data = test.getData();

            for (int j = 0; j < tests[i].length; j++)
                data.editList(Integer.parseInt(tests[i][j][0]),tests[i][j][1]);

            for (Entry e : data.getEntries())
                assert (e.getListPosition() == results[i][e.getId()]);


        }

        appContext.deleteDatabase(DATABASE_NAME);
    }

    @Test
    public void returnListAsArray(){

        String[] tasks = {"0","1","2","3"};

        String[] lists = {"0","1","2","3"};

        TestClass test = new TestClass(appContext,tasks,lists);

        test.set();

        Data data = test.getData();

        String[] returnedLists = data.returnListsAsArray();

        assert(returnedLists.length == lists.length+2);

        for (String s : lists) {
            boolean contains = false;
            for (String rs :returnedLists)
                if (rs.equals(s)) {
                    contains = true;
                    break;
                }
            assert (contains);
        }

        appContext.deleteDatabase(DATABASE_NAME);
    }

    @Test
    public void getLists(){

        String[] tasks = {"0","1","2","3"};

        String[] lists = {"0","1","2","3"};

        TestClass test = new TestClass(appContext,tasks,lists);

        test.set();

        Data data = test.getData();

        ArrayList<String> returnedLists = data.getLists();

        assert(returnedLists.size() == lists.length+2);

        for (String s : lists) {
            boolean contains = false;
            for (String rs :returnedLists)
                if (rs.equals(s)) {
                    contains = true;
                    break;
                }
            assert (contains);
        }

        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void getTasksToPick(){

        String[] tasks = {"0","1","2","3"};

        int[] dates = {Data.getToday()-1,Data.getToday()+1,Data.getToday(),Data.getToday()+1};

        String[] results = {"0","2"};

        TestClass test = new TestClass(appContext, tasks);

        test.set();

        Data data = test.getData();

        ArrayList<Entry> tasksToPick = data.getTasksToPick();

        for (int i=0; i< tasksToPick.size();i++)
            assert(tasksToPick.get(i).getTask().equals(tasks[i]));

        for (int i = 0; i < tasks.length; i++)
            data.editReminderDate(Integer.parseInt(tasks[i]), dates[i]);

        tasksToPick = data.getTasksToPick();

        assert(tasksToPick.size() == results.length);

        for (int i = 0; i < tasksToPick.size(); i++)
            assert(tasksToPick.get(i).getTask().equals(results[i]));


        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void getList(){
        String[] tasks = {"0","1","2","3","4","5"};

        String[] lists = {"0","0","0","1","1","1"};

        TestClass test = new TestClass(appContext,tasks,lists);

        test.set();

        Data data = test.getData();

        ArrayList<Entry> returnedList = data.getList("0");

        for (int i = 0; i < returnedList.size(); i++){
            assert(returnedList.get(i).getTask().equals(String.valueOf(i)));
        }


        returnedList = data.getList("1");

        for (int i = 0; i < returnedList.size(); i++){
            assert(returnedList.get(i).getTask().equals(String.valueOf(i+3)));
        }

        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void getDropped(){

        String[] tasks = {"0","1","2"};

        TestClass test = new TestClass(appContext, tasks);

        test.set();

        Data data = test.getData();

        ArrayList<Entry> dropped = data.getDropped();

        for (int i = 0; i < tasks.length; i++)
            assert(dropped.get(i).getTask().equals(tasks[i]));

        tasks = new String[0];

        test = new TestClass(appContext, tasks);

        test.set();

        data = test.getData();

        dropped = data.getDropped();

        assert(dropped.size()==0);

        appContext.deleteDatabase(DATABASE_NAME);
    }



    @Test
    public void getFocus(){


        String[] tasks = {"0","1","2","3"};

        boolean[] focus = {true,false,false,false};

        String[] results = {"0","2"};

        TestClass test = new TestClass(appContext, tasks);

        test.set();

        Data data = test.getData();

        data.editRecurrence(2, "w2");
        data.editRecurrence(3, "w2");

        for (String s : tasks)
            data.setFocus(Integer.parseInt(s),focus[Integer.parseInt(s)]);

        data.addToRecurringButRemoved(3);


        ArrayList<Entry> focused = data.getFocus();

        for (int i = 0; i < focused.size(); i++)
            assert(focused.get(i).getTask().equals(results[i]));

        data.removeFromRecurringButRemoved(3);

        focused = data.getFocus();

        String[] resultsNew = new String[results.length+1];

        System.arraycopy(results, 0, resultsNew, 0, results.length);

        resultsNew[results.length] = "3";

        for (int i = 0; i < focused.size(); i++)
            assert(focused.get(i).getTask().equals(resultsNew[i]));


        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void getNoList(){

        String[] tasks = {"0","1","2","3"};

        String[] lists = {"0","1",null,null};

        String[] results = {"2","3"};

        TestClass test = new TestClass(appContext,tasks, lists);

        test.set();

        Data data = test.getData();

        ArrayList<Entry> noList = data.getNoList();

        for (int i=0;i<noList.size();i++)
            assert(noList.get(i).getTask().equals(results[i]));

        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void getEntriesOrderedByDate(){

        String[] tasks = {"0","1","2"};

        int[][] tests = {{0,20221231,20230110},{20221231,20230110,0},{20230110,20221231,0},
                            {0,20230110,20221231},{20221231,0,20230110},{20230110,0,20221231}
                            };

        String[][] results = {{"0","1","2"},{"2","0","1"},{"2","1","0"},
                                {"0","2","1"},{"1","0","2"},{"1","2","0"}
                                };

        TestClass test = new TestClass(appContext, tasks);


        for (int i = 0; i < tests.length; i++) {

            test.set();

            Data data = test.getData();

            for (int j = 0; j < tests[i].length; j++)
                data.editReminderDate(j, tests[i][j]);

            ArrayList<Entry> entries = data.getEntriesOrderedByDate();

            for (int j = 0; j < entries.size(); j++)
                assert(entries.get(j).getTask().equals(results[i][j]));

        }

        appContext.deleteDatabase(DATABASE_NAME);

    }

    @Test
    public void setRecurring(){

        int[] today = {20231015,20231015,20231015,20231015,20231015,20231015,20231015,
                        20230131,20230228,20240228,20231231,
                        20231015,20231015,20231015,20231030,20231231,
                        20230101,20230101,20231201,
                        20230101,20230101
        };

        String[] mode = {"d1","d2","d3","d4","d5","d6","d7",
                        "d1","d1","d1","d1",
                        "w1","w2","w1","w1","w1",
                        "m1","m2","m1",
                        "y1","y2"
        };

        int[] tests ={20231015,20231015,20231015,20231015,20231015,20231015,20231015,
                        20230131,20230228,20240228,20231231,
                        20231015,20231015,20231008,20231025,20231204,
                        20230101,20230101,20230101,
                        20230101,20221501
        };

        int[] results = {20231016,20231017,20231018,20231019,20231020,20231021,20231022,
                        20230201,20230301,20240229,20240101,
                        20231022,20231029,20231022,20231101,20240101,
                        20230201,20230301,20240101,
                        20240101,20241501
        };

        Data data = new Data(appContext, DATABASE_NAME);

        data.add("Test");

        for (int i = 0; i < tests.length; i++){


            data.editReminderDate(0,tests[i]);

            data.getEntries().get(0).setRecurrence(mode[i]);

            data.setRecurring(0,today[i]);

            assert(data.getEntries().get(0).getReminderDate()==results[i]);

        }

        appContext.deleteDatabase(DATABASE_NAME);

    }


    @After
    public void cleanUp(){

        appContext.deleteDatabase(DATABASE_NAME);
    }

}
