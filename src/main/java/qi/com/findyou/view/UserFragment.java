package qi.com.findyou.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import qi.com.findyou.R;
import qi.com.findyou.base.BaseFragment;

/**
 * Created by qi_fu on 2017/11/15.
 */

public class UserFragment extends BaseFragment {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_birth)
    TextView tvBirth;
    @BindView(R.id.tv_id)
    TextView tvId;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        //初始化view的各控件
        isPrepared = true;
        loadData();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isViable) {
            return;
        }
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
