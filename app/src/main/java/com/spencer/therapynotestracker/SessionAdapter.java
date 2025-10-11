package com.spencer.therapynotestracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.BinViewHolder> {

    private List<Session> sessions;

    public SessionAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    public static class BinViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView agenda;
        public TextView notes;

        public BinViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.sessionDate);
            agenda = itemView.findViewById(R.id.sessionAgenda);
            notes = itemView.findViewById(R.id.sessionNotes);

        }
    }

    @Override
    public BinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        return new BinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BinViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.date.setText(session.getDate());
        holder.agenda.setText(session.getAgenda());
        holder.notes.setText(session.getNotes());
    }

    @Override
    public int getItemCount() {
        if (sessions != null)
            return sessions.size();
        else
            return 0;
    }

    public void updateBins(List<Session> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    public List<Session> getSessions() {
        return this.sessions;
    }
}
