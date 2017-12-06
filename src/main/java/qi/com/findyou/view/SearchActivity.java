package qi.com.findyou.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qi.com.findyou.R;
import qi.com.findyou.adapter.PersonAdapter;
import qi.com.findyou.base.LocationApplication;
import qi.com.findyou.model.Person;

public class SearchActivity extends Activity {

    private ListView listView;
    private List<Person> list=new ArrayList<>();
    private List<Person> listData=new ArrayList<>();
    private PersonAdapter personAdapter;
    private EditText editText;
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        noData = (TextView) findViewById(R.id.no_data);
        listView = (ListView) findViewById(R.id.list_search);
        editText = (EditText) findViewById(R.id.search_user);
        personAdapter=new PersonAdapter(list,this);
        listView.setAdapter(personAdapter);
    }

    private void initData() {
        //填充各控件的数据
        LocationApplication application = (LocationApplication)getApplication();
        listData.addAll(application.getPersonList());
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, PersonMsgActivity.class);
                intent.putExtra("person",(Serializable)list.get(position));
                startActivity(intent);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                list.clear();
                personAdapter.notifyDataSetChanged();
                noData.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void searchList(String content){
        if (content == null||content.equals(""))return;
        for (int i = 0; i < listData.size(); i++) {
            if(listData.get(i).getName().contains(content)){
                list.add(listData.get(i));
            }
        }
        if(list.size()!=0){
            noData.setVisibility(View.GONE);
            personAdapter.notifyDataSetChanged();
        }else {
            noData.setVisibility(View.VISIBLE);
        }
    }
}
