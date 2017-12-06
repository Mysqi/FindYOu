package qi.com.findyou.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import qi.com.findyou.R;
import qi.com.findyou.base.BaseFragment;

/**
 * Created by qi_fu on 2017/11/15.
 */

public class DataFragment extends BaseFragment {

    private RelativeLayout careList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        careList = (RelativeLayout) view.findViewById(R.id.care_list);
        //初始化view的各控件
        isPrepared = true;
        setListeren();
        return view;
    }

    private void setListeren() {
        careList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TypeCareActivity.class);
                intent.putExtra("type",'3');
                intent.putExtra("title","重点关注名单");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void loadData() {
        if(!isPrepared || !isViable) {
            return;
        }
        //填充各控件的数据
    }

    @Override
    public void refreshData() {

    }
}
