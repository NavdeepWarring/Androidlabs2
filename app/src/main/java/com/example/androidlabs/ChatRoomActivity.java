package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.androidlabs.ProfileActivity.ACTIVITY_NAME;

public class ChatRoomActivity extends AppCompatActivity {

    private MessageAdapter myAdapter;
    private ArrayList<Message> list = new ArrayList<>();
    SQLiteDatabase db;
    public static final String ACTIVITY_NAME = "CHAT_ROOM_ACTIVITY";
    Cursor results;
    String query = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        loadDataFromDatabase();


        Button sendBtn = findViewById(R.id.send);
        sendBtn.setOnClickListener(click -> {
            EditText messageGoesHere = findViewById(R.id.typeMessage);

            String name = messageGoesHere.getText().toString();
            boolean typeOfMessage = true;


            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyOpener.COL_MESSAGE, name);
            newRowValues.put(MyOpener.COL_TYPE, typeOfMessage);

            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);


            list.add(new Message(name, typeOfMessage, newId));
            myAdapter.notifyDataSetChanged();
            messageGoesHere.getText().clear();

        });


        Button receiveBtn = findViewById(R.id.receive);
        receiveBtn.setOnClickListener(click -> {
            EditText messageGoesHere = findViewById(R.id.typeMessage);
            String name = messageGoesHere.getText().toString();
            boolean typeOfMessage = false;


            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyOpener.COL_MESSAGE, name);
            newRowValues.put(MyOpener.COL_TYPE, typeOfMessage);

            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);


            list.add(new Message(name, typeOfMessage, newId));
            myAdapter.notifyDataSetChanged();
            messageGoesHere.getText().clear();
        });


        ListView messages = findViewById(R.id.chatList);
        messages.setAdapter( myAdapter = new MessageAdapter());
        messages.setOnItemLongClickListener((p, v, position, id)-> {
            Message selectedContact = list.get(position);
            
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("You clicked on item # " + position)
                    .setMessage("Are you sure you want to delete from database 'id = " + id + "' ?")
                    .setPositiveButton("Delete", (click, arg) -> {
                        deleteContact(selectedContact);
                        list.remove(position);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNeutralButton("Cancel", (click, arg) -> {})
                    .create().show();

            return true;
        });
        printCursor(results, db.getVersion());
    }

    private void loadDataFromDatabase() {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {MyOpener.COL_ID, MyOpener.COL_MESSAGE, MyOpener.COL_TYPE};
        results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int MessageColIndex = results.getColumnIndex(MyOpener.COL_MESSAGE);
        int TypeColIndex = results.getColumnIndex(MyOpener.COL_TYPE);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);


        while(results.moveToNext())
        {
            String message = results.getString(MessageColIndex);

            int send = Integer.parseInt(results.getString(TypeColIndex));
            boolean type = send == 1;
            long id = results.getLong(idColIndex);

            Message loaded = new Message(message, type, id);
            //add the new Contact to the array list:
            list.add(loaded);

            query = query + "\n" +DatabaseUtils.dumpCurrentRowToString(results);
        }
    }




    protected void deleteContact(Message m)
    {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(m.getId())});
    }

    private void printCursor(Cursor c, int version) {
        int columnCount =   c.getColumnCount();
        String[] columnNames = c.getColumnNames();
        int resultCount = c.getCount();


        Log.i("The Database version: ", String.valueOf(db.getVersion()));
        Log.i("The number of columns: ", Integer.toString(columnCount));
        Log.i("Columns names: ", Arrays.toString(columnNames));
        Log.i("Number of result: ",  Integer.toString(resultCount));
        Log.i("Query results: \n", query);
    }

    class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Message getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) { return getItem(position).getId(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();

            Message thisRow = getItem(position);
            //make a new row:
            View rightView = inflater.inflate(R.layout.row_layout_right, parent, false);
            View leftView = inflater.inflate(R.layout.row_layout_left, parent, false);

            //set what the text should be for this row:
            if(thisRow.getTypeOfMessage()){
                TextView tView = rightView.findViewById(R.id.textGoesHere);
                tView.setText( thisRow.getMessage() );
                return rightView;
            }else{
                TextView tView = leftView.findViewById(R.id.textGoesThere);
                tView.setText( thisRow.getMessage());
                return leftView;
            }
        }
    }
}

