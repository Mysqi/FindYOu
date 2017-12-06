package qi.com.findyou.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qi.com.findyou.R;
import qi.com.findyou.adapter.PersonAdapter;
import qi.com.findyou.base.BaseFragment;
import qi.com.findyou.base.LocationApplication;
import qi.com.findyou.model.Person;

public class ListDataFragment extends BaseFragment {

    private ListView listView;
    private List<Person> list = new ArrayList<>();
    private PersonAdapter personAdapter;
    private RelativeLayout llSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        isPrepared = true;
        listView = (ListView) view.findViewById(R.id.list_list);
        llSearch = (RelativeLayout) view.findViewById(R.id.search_user);

        personAdapter = new PersonAdapter(list, getContext());
        listView.setAdapter(personAdapter);
        //初始化view的各控件
//        loadData();
        initListener();
        return view;
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isViable) {
            return;
        }
        getNetData();
    }

    @Override
    public void refreshData() {
        getNetData();
    }

    private  void getNetData(){
        clearData();
        LocationApplication application = (LocationApplication) getActivity().getApplication();
        List<Person> personList = application.getPersonList();
        if(personList== null||personList.size() == 0){
            return;
        }
        list.addAll(application.getPersonList());
        personAdapter.notifyDataSetChanged();
    }

    //重制数据
    public void clearData() {
        list.clear();
        personAdapter.notifyDataSetChanged();
    }


    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PersonMsgActivity.class);
                intent.putExtra("person", (Serializable) list.get(position));
                startActivity(intent);
            }
        });
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
//        llSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchList(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }
//
//    // TODO: Customize parameter argument names
//    private static final String ARG_COLUMN_COUNT = "column-count";
//    // TODO: Customize parameters
//    private int mColumnCount = 1;
//    private OnListFragmentInteractionListener mListener;
//
//    /**
//     * Mandatory empty constructor for the fragment manager to instantiate the
//     * fragment (e.g. upon screen orientation changes).
//     */
//    public ListDataFragment() {
//    }
//
//    // TODO: Customize parameter initialization
//    @SuppressWarnings("unused")
//    public static ListDataFragment newInstance(int columnCount) {
//        ListDataFragment fragment = new ListDataFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new MyListRecyclerViewAdapter(DummyContent.ITEMS, mListener));
//        }
//        //初始化view的各控件
//        isPrepared = true;
//        loadData();
//        return view;
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;location (2).png
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    @Override
//    protected void loadData() {
//        if(!isPrepared || !isViable) {
//            return;
//        }
//        //填充各控件的数据
//    }
//
//    public interface OnListFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onListFragmentInteraction(DummyItem item);
//    }
}
