package qi.com.findyou.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import qi.com.findyou.R;
import qi.com.findyou.model.Person;

public class PersonMsgActivity extends Activity {

    private TextView tvName;
    private TextView tvType;
    private TextView tvId;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvLocation;
    private ImageView imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_msg);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        imageType= (ImageView) findViewById(R.id.image_type);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvId = (TextView) findViewById(R.id.tv_id);
        tvHeight = (TextView) findViewById(R.id.tv_height);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvLocation = (TextView) findViewById(R.id.tv_location);

    }

    private void initData() {
        Person person = (Person) getIntent().getSerializableExtra("person");
        tvName.setText(person.getName());
        tvId.setText(person.getId());
        if (person.getWarntype() == 1) {
            imageType.setImageDrawable(this.getResources().getDrawable(R.drawable.cycle_green));
        } else if (person.getWarntype() == 2) {
            imageType.setImageDrawable(this.getResources().getDrawable(R.drawable.cycle_org));
        } else {
            imageType.setImageDrawable(this.getResources().getDrawable(R.drawable.cycle_red));
        }
    }

    private void initListener() {
        imageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PersonMsgActivity.this,GSMCellLocationActivity.class));
            }
        });

    }
}
