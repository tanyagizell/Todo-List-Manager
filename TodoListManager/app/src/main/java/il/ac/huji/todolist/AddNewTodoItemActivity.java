package il.ac.huji.todolist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;


public class AddNewTodoItemActivity extends ActionBarActivity {

    //views
    EditText edtItem;
    DatePicker datePick;
    Button btnOk, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_todo_item);

        //init
        edtItem = (EditText) findViewById(R.id.edtNewItem);
        datePick = (DatePicker) findViewById(R.id.datePicker);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOK);

        //set listeners
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the cancel button is hit, return to parent activity
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the cancel button is hit, return to parent activity
                addListItem();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_todo_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListItem(){

        //get the title
        String item = edtItem.getText().toString();

        //get date
        int year = datePick.getYear();
        int month= datePick.getMonth();
        int day = datePick.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        Date date= calendar.getTime();

        //pass them along as extras
        Intent intent = new Intent();
        intent.putExtra("title",item);
        intent.putExtra("dueDate",date);

        setResult(RESULT_OK,intent);
        finish();


    }

}
