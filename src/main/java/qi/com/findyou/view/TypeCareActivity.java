package qi.com.findyou.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qi.com.findyou.R;
import qi.com.findyou.adapter.PersonAdapter;
import qi.com.findyou.base.LocationApplication;
import qi.com.findyou.model.Person;

public class TypeCareActivity extends Activity {

    private TextView tvCare;
    private ListView listView;
    private PersonAdapter adapter;
    private List<Person> personList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_care);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvCare = (TextView) findViewById(R.id.tv_type);
        listView = (ListView) findViewById(R.id.list_care);
        adapter = new PersonAdapter(personList, this);
        listView.setAdapter(adapter);
    }

    private void initData() {
        int type = getIntent().getIntExtra("type", 3);//获取传递的名单类型
        String title = getIntent().getStringExtra("title");
        tvCare.setText(title);
        //填充各控件的数据
        LocationApplication application = (LocationApplication)getApplication();
        List<Person> persons = application.getPersonList();
        for (int i = 0; i < persons.size(); i++) {
            if(persons.get(i).getWarntype() == type){
                this.personList.add(persons.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TypeCareActivity.this, PersonMsgActivity.class);
                intent.putExtra("person",(Serializable)personList.get(position));
                startActivity(intent);
            }
        });
    }
}
