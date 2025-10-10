package com.spencer.therapynotestracker;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends BaseAdapter {
    List<SessionModel> items;

    private Context context;

    public NoteAdapter(List<SessionModel> items, Context context) {
        super();
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(context);
        textView.setText(items.get(i).getDate() + " " + items.get(i).getAgenda() + " " + items.get(i).getNotes());
        return textView;
    }

}
