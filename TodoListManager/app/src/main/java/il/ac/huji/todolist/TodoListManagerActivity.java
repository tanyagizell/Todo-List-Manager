package il.ac.huji.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class TodoListManagerActivity extends ActionBarActivity {

    //const
    public static final int ADD_ITEM_REQUEST_CODE = 1;
    public static final String TITLE_EXTRA = "title";
    public static final String DATE_EXTRA = "dueDate";
    public static final String CALL_IDENTIFIER_STRING = "Call ";

    //views
    ListView list;

    //globals
    ArrayList<String> todoTitleList;
    ArrayList<Calendar> todoDateList;
    ArrayList<HashMap<String,String>> todoEntryList;
    ArrayAdapter<String> todoListAdapter;
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        //init
        list = (ListView) findViewById(R.id.lstTodoItems);
        todoTitleList = new ArrayList<String>();
        todoDateList = new ArrayList<Calendar>();

        //read database to get all the previous entries
        todoEntryList = dbHelper.getAllEntries();

        for (int entryIdx = 0; entryIdx<todoEntryList.size(); entryIdx ++){

            HashMap<String,String> entryMap = todoEntryList.get(entryIdx);
            //extract elements from DB
            String titleStr = entryMap.get(DBHelper.TODO_TITLE_STR);
            String dueDateStr = entryMap.get(DBHelper.DUE_DATE_STR);
            String id = entryMap.get(DBHelper.KEY_ID_STR);

            //convert date
            long dueDateMillis = Long.parseLong(dueDateStr,10);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dueDateMillis);

            //add entry data to adapter lists
            todoTitleList.add(titleStr);
            todoDateList.add(cal);
        }


                //set array adapter for our listview
        todoListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, todoTitleList) {

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {


                LayoutInflater inflater =LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.todo_item,null);

                TextView title = (TextView) view.findViewById(R.id.txtTodoTitle);
                TextView dueDate = (TextView) view.findViewById(R.id.txtTodoDueDate);

                //set the title text view
                String itemTitle= todoTitleList.get(position);
                title.setText(itemTitle);

                Calendar today = new GregorianCalendar();
                //zero anything other than the date, since thats the comparison we want
                int currentYear = today.get(Calendar.YEAR);
                int currentMonth = today.get(Calendar.MONTH)+1;
                int currentDay = today.get(Calendar.DAY_OF_MONTH);

                boolean overDue = false;
                //set the date text view
                Calendar dueDateCal = todoDateList.get(position);
                if (dueDateCal != null){

                   int year = dueDateCal.get(Calendar.YEAR);
                   int month = dueDateCal.get(Calendar.MONTH) + 1;
                   int day = dueDateCal.get(Calendar.DAY_OF_MONTH);

                   String strDate = "" + day + "/" + month + "/" + year;
                   dueDate.setText(strDate);

                   // check is the due date has passed
                   if ((year<currentYear) ||
                      ((year==currentYear) && (month<currentMonth) ) ||
                      ((year==currentYear) && (month==currentMonth) && (day < currentDay) ))
                       overDue = true;
                }

                //set color according date
                if (overDue) {//even items are red
                    title.setTextColor(Color.RED);
                    dueDate.setTextColor(Color.RED);
                }

                return view;
            }
        };
        list.setAdapter(todoListAdapter);

        //set listeners
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                boolean call = false;
                String itemTitle =  parent.getItemAtPosition(position).toString();

                if (itemTitle.startsWith(CALL_IDENTIFIER_STRING)){
                    call = true;
                }
                handleSelectedItem(itemTitle,position,call);
                return true;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.

        int id = item.getItemId();

        switch(id){
            case R.id.menuItemAdd:
                openAddActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ITEM_REQUEST_CODE){
            if (resultCode == RESULT_OK) {

                //get the listing and the date
                String title = data.getStringExtra(TITLE_EXTRA);
                Date dueDate = (Date) data.getSerializableExtra(DATE_EXTRA);


                Calendar cal = Calendar.getInstance();
                if (dueDate != null)
                    cal.setTime(dueDate);
                else cal = null;

                todoTitleList.add(title);
                todoDateList.add(cal);


                //create a database entry
                String dueDateMillisStr = "" + cal.getTimeInMillis();
                //create a new db entry
                HashMap<String,String> dbEntry = createEntry(title,dueDateMillisStr);


                //add to the database
                dbHelper.insertItem(dbEntry);

                todoEntryList = dbHelper.getAllEntries();


                //notify adapeter
                todoListAdapter.notifyDataSetChanged();
            }
        }
    }

    /*
        *  openAddActivity
        *
        * Opens a new activity that handles the addition of new list items
        *
        * */
    public void openAddActivity(){

        //create an intent
        Intent addItem =new Intent(this,AddNewTodoItemActivity.class);

        //start the new item activity
        startActivityForResult(addItem, ADD_ITEM_REQUEST_CODE);

    }

    /*
    *  handleSelectedItem
    *
    * Handles the case when an item was selected in the list (by long click)
    * Prompts a dialog that offers to delete the selected listing,
    * or to dial the number (when relevant)
    *
    * @param item - the title of the selected item in the list
    * @param pos  - the position of the selected item in the list
    * @param call - should we prompt a call dialog?
    *
    * */
    public void handleSelectedItem(final String item, final int pos, boolean call){

        View bodyView;
        TextView titleTxt;
        Button deleteButton;
        Button callButton = null;


        final Dialog dialogBuilder = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //if call is true, use a dialog with a call option
        if (call){
            bodyView = inflater.inflate((R.layout.custom_dialog_with_call), null);
            callButton = (Button) bodyView.findViewById(R.id.menuItemCall);
            callButton.setText(item);
        }

        else { //otherwise use a dialog with just a delete option
            bodyView = inflater.inflate((R.layout.custom_dialog), null);

        }

        //title and delete button appear in both dialogs
        titleTxt =  (TextView) bodyView.findViewById(R.id.menuItemTitle);
        titleTxt.setText(item);
        deleteButton = (Button) bodyView.findViewById(R.id.menuItemDelete);

        dialogBuilder.setContentView(bodyView);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //remove the item
                deleteItem(pos);
                //dismiss the dialog
                dialogBuilder.dismiss();
            }
        });

        if (call) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //dial the number
                    String number = item.replaceFirst(CALL_IDENTIFIER_STRING,"");
                    String numberUri = "tel:" + number;
                    Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse(numberUri));

                    dialogBuilder.dismiss();
                    startActivity(dial);
                }
            });
        }

        dialogBuilder.show();

    }

    /*
    * Create a new entry that can be inserted into the DB using the DBHelper class
    */
    private HashMap<String,String> createEntry(String title, String dueDateMillis){

        HashMap<String,String> dbEntry = new HashMap<String,String>();

        //populate values
        dbEntry.put(DBHelper.TODO_TITLE_STR,title);
        dbEntry.put(DBHelper.DUE_DATE_STR,dueDateMillis);

        return dbEntry;
    }

    /*
     * Delete the item with the given index  from the to-do list
     */
    private void deleteItem(int index){

        String deleteId = todoEntryList.get(index).get(DBHelper.KEY_ID_STR);

        //remove item
        todoTitleList.remove(index);
        todoDateList.remove(index);
        todoEntryList.remove(index);

        //remove from DB
        dbHelper.deleteItem(deleteId);

        //notify adapter
        todoListAdapter.notifyDataSetChanged();

    }

}