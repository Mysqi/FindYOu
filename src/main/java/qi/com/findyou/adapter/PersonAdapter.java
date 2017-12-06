package qi.com.findyou.adapter;

/**
 * Created by qi_fu on 2017/11/20.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import qi.com.findyou.R;
import qi.com.findyou.model.Person;

public class PersonAdapter extends BaseAdapter {
    List<Person> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public PersonAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public PersonAdapter(List<Person> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setEntities(List<Person> entities) {
        this.list = entities;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Person person = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adapter_list_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.id);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(person.getName());
        holder.content.setText(person.getId());
        if (person.getWarntype() == 1) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cycle_green));
        } else if (person.getWarntype() == 2) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cycle_org));
        } else {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.cycle_red));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView content;
        ImageView icon;
    }
}
