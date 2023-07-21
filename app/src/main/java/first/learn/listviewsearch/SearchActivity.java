package first.learn.listviewsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    EditText editText;
    ListView listView2;

    ArrayList<String> arrayList ;
    SearchAdapter mSearchAdapter ;
    Context mContext ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        mContext = this ;

        listView2 = (ListView) findViewById(R.id.listView2);
        editText = (EditText) findViewById(R.id.editText);
//        editText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        arrayList = new ArrayList<>();


        for (int i = 0; i<101; i++){
             arrayList.add("item : "+ i);

        }



        mSearchAdapter = new SearchAdapter(mContext, R.layout.search_list_item, R.id.alquran_text, arrayList);

        listView2.setAdapter(mSearchAdapter);

// int oldPosition = (int) mRecentAdapter.getItemId(position);]]'';;p[]=-]\\';lASDFGGH=///.;''\';'\



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                System.out.println("EditText text  beforeTextChanged: "+ s);
                mSearchAdapter.getFilter().filter(s);


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchAdapter.getFilter().filter(s);
                System.out.println("EditText text  onTextChanged: "+ s);


            }

            @Override
            public void afterTextChanged(Editable s) {

                System.out.println("EditText text  afterTextChanged: "+ s);

                mSearchAdapter.getFilter().filter(s);
            }
        });



        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Show Position new: "+ position);
                System.out.println("Show String  new: "+ arrayList.get(position));

                int originalPosition =(int) mSearchAdapter.getItemId(position);

                System.out.println("Show Position Original  : "+ originalPosition);
                System.out.println("Show String Original : "+ arrayList.get(originalPosition));


            }
        });




    }
}