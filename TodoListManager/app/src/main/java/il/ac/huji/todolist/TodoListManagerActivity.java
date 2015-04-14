package il.ac.huji.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class TodoListManagerActivity extends ActionBarActivity {

    //views
    ListView list;

    //globals
    ArrayList<String> todoItemList;
    ArrayAdapter<String> todoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        //init
        list = (ListView) findViewById(R.id.lstTodoItems);
        todoItemList = new ArrayList<String>();

        //set array adapter for our listview
        todoListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,todoItemList) {

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                //set color according to position
                if ((position % 2 ) == 0)//even items are red
                    textView.setTextColor(Color.RED);
                else    //odd items are blue
                    textView.setTextColor(Color.BLUE);

                return view;

            }
        };
        list.setAdapter(todoListAdapter);

        //set listeners
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String itemTitle =  parent.getItemAtPosition(position).toString();
                handleSelectedItem(itemTitle,position);

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
                addListItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public void addListItem(){

        EditText edtItem = (EditText) findViewById(R.id.edtNewItem);
        String item = edtItem.getText().toString();

        //add the new item to the list
        todoItemList.add(item);

        //notify the adapter
        todoListAdapter.notifyDataSetChanged();


    }

    /*
    *  handleSelectedItem
    *
    * Handles the case when an item was selected in the list (by long click)
    * Prompts a dialog that offers to delete the selected listing
    *
    * @param item - the title of the selected item in the list
    * @param pos  - the position of the selected item in the list
    *
    * */
    public void handleSelectedItem(String item, final int pos){

        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(this);

        deleteDialogBuilder.setTitle(item); //set the title to be the clicked item
        deleteDialogBuilder.setCancelable(true); //set to cancellable
        //set positive button
        deleteDialogBuilder.setPositiveButton(R.string.delete_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remove item
                todoItemList.remove(pos);
                //notify adapter
                todoListAdapter.notifyDataSetChanged();
            }
        });
        //finally, show the dialog
        deleteDialogBuilder.show();

    }
}