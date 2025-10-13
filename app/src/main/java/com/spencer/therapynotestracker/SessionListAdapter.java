package com.spencer.therapynotestracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spencer.therapynotestracker.database.Session;

import java.util.List;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessionViewHolder> {


    private List<Session> sessions;
    private SelectListener mListener;

    public SessionListAdapter(List<Session> sessions, SelectListener listener) {
        this.sessions = sessions;
        this.mListener = listener;
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView agenda;
        public TextView notes;

        public TextView therapist;

        Button myButton;

        public SessionViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.sessionDate);
            agenda = itemView.findViewById(R.id.sessionAgenda);
            notes = itemView.findViewById(R.id.sessionNotes);
            therapist = itemView.findViewById(R.id.sessionTherapist);

            myButton = itemView.findViewById(R.id.myButton);
        }
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.date.setText(session.getDate());
        holder.agenda.setText(session.getAgenda());
        holder.notes.setText(session.getNotes());
        holder.therapist.setText(session.getTherapist());

        holder.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(sessions.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (sessions != null)
            return sessions.size();
        else
            return 0;
    }

    public void updateSessions(List<Session> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    public List<Session> getSessions() {
        return this.sessions;
    }
}
