package com.spencer.therapynotestracker.sessionlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spencer.therapynotestracker.MainActivity;
import com.spencer.therapynotestracker.R;
import com.spencer.therapynotestracker.SelectListener;
import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.databinding.FragmentFirstBinding;
import com.spencer.therapynotestracker.sessionedit.ActiveSessionViewModel;

import java.util.List;

public class SessionListFragment extends Fragment implements View.OnClickListener, SelectListener {

    private FragmentFirstBinding binding;

    private List<Session> list;

    private ActiveSessionViewModel activeSession;

    private SessionListViewModel sessionListViewModel;

    FloatingActionButton enterButton;

    private SessionListAdapter sessionListAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_first, container, false);

        sessionListViewModel = new ViewModelProvider(requireActivity()).get(SessionListViewModel.class);
        activeSession = new ViewModelProvider(requireActivity()).get(ActiveSessionViewModel.class);

        // Observe LiveData bins
        sessionListViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            // Update adapter
            sessionListAdapter.updateSessions(sessions);
        });

        enterButton = contentView.findViewById(R.id.fab);
        enterButton.setOnClickListener(this);

        return contentView;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Assign sessionlist to SessionAdapter
        sessionListAdapter = new SessionListAdapter(sessionListViewModel.getSessions().getValue(), this::onItemClicked);

        // Set the LayoutManager that this RecyclerView will use.
        RecyclerView recyclerView = view.findViewById(R.id.binRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // adapter instance is set to the recyclerview to inflate the items.
        recyclerView.setAdapter(sessionListAdapter);

        // Add gray divider between items
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            Toast.makeText(v.getContext(), "Add Button Clicked!", Toast.LENGTH_SHORT).show();
            promptForSession();
        } else if (v.getId() == R.id.action_export_data) {
            Toast.makeText(v.getContext(), "Export data Button Clicked!", Toast.LENGTH_SHORT).show();

        }
    }

    private void promptForSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());


        LayoutInflater inflater = this.getLayoutInflater();
        View dialogueView = inflater.inflate(R.layout.alert_session_prompt, null);
        builder.setView(dialogueView);

        final EditText alertPromptDate = dialogueView.findViewById(R.id.alertSessionDate);
        final EditText alertPromptAgenda = dialogueView.findViewById(R.id.alertSessionAgenda);
        final EditText alertPromptNotes = dialogueView.findViewById(R.id.alertSessionNotes);
        final EditText alertPromptTherapist = dialogueView.findViewById(R.id.alertSessionTherapist);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Session session = new Session(alertPromptDate.getText().toString(),
                        alertPromptAgenda.getText().toString(),
                        alertPromptNotes.getText().toString(),
                        alertPromptTherapist.getText().toString());

                sessionListViewModel.insert(session);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ((MainActivity) getActivity()).sendBinAlertNotification(list);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onItemClicked(Session session) {
        NavHostFragment.findNavController(SessionListFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);

        activeSession.selectItem(session);

        Toast.makeText(this.getContext(), activeSession.getSelectedItem().getValue().getDate() + " - " + activeSession.getSelectedItem().getValue().getTherapist(), Toast.LENGTH_SHORT).show();

    }
}