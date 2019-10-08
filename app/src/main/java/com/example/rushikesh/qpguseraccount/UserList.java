package com.example.rushikesh.qpguseraccount;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

/**
 * Created by Rushikesh on 06/04/2018.
 */

public class UserList extends ArrayAdapter<User> {
    private Activity context;
    private List<User> userList;

    public UserList(Activity context, int list_layout, List<User> userList){
        super(context, R.layout.list_layout,userList);
        this.context=context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return myCustomSpinner(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCustomSpinner(position, convertView, parent);
    }

    private View myCustomSpinner(int position, @Nullable View myView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listViewItem = inflater.inflate(R.layout.list_layout,parent,false);

        TextView textViewCourseName = listViewItem.findViewById(R.id.textViewCourseName);

        User user = userList.get(position);
        textViewCourseName.setText(user.getUserName());
        return listViewItem;
    }

}
