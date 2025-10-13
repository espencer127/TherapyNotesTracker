package com.spencer.therapynotestracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spencer.therapynotestracker.database.Session;

import java.util.List;

public class NoteAdapter extends BaseAdapter {
    List<Session> items;

    private Context context;

    public NoteAdapter(List<Session> items, Context context) {
        super();
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (items != null && !items.isEmpty())
            return items.size();
        else
            return 0;
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
