package com.spencer.therapynotestracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spencer.therapynotestracker.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements View.OnClickListener {

    private FragmentFirstBinding binding;

    private List<Session> list;

    private HomeViewModel homeViewModel;

    FloatingActionButton enterButton;

    private SessionAdapter sessionAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_first, container, false);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        //RecyclerView recyclerView = binding.binRecyclerView;
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(this.sessionAdapter);

        // Add gray divider between items
        //DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        //divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider));
        //recyclerView.addItemDecoration(divider);

        // Observe LiveData bins
        homeViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            // Update adapter
            sessionAdapter.updateBins(sessions);

            Log.d("First Fragment ln 81", sessionAdapter.getSessions().toString());
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

        // getting the employeelist
        this.list = new ArrayList<>();

        Session testSesh = new Session("date666", "agenda666", "notes666");
        List<Session> seshList = new ArrayList<>();
        seshList.add(testSesh);

        // Assign employeelist to ItemAdapter
        sessionAdapter = new SessionAdapter(homeViewModel.getSessions().getValue());


        // Set the LayoutManager that
        // this RecyclerView will use.
        RecyclerView recyclerView = view.findViewById(R.id.binRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // adapter instance is set to the
        // recyclerview to inflate the items.
        recyclerView.setAdapter(sessionAdapter);

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
        }
    }

    private void promptForSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Enter Session Details");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogueView = inflater.inflate(R.layout.alert_session_prompt, null);
        builder.setView(dialogueView);

        final EditText alertPromptDate = dialogueView.findViewById(R.id.alertSessionDate);
        final EditText alertPromptAgenda = dialogueView.findViewById(R.id.alertSessionAgenda);
        final EditText alertPromptNotes = dialogueView.findViewById(R.id.alertSessionNotes);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("Home View Model ln 137", sessionAdapter.getSessions().toString());

                Session tempSession = new Session
                (alertPromptDate.getText().toString(),
                alertPromptAgenda.getText().toString(),
                alertPromptNotes.getText().toString());
                list.add(tempSession);

                Session session = new Session(tempSession.getDate(), tempSession.getAgenda(), tempSession.getNotes());
                homeViewModel.insert(session);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ((MainActivity) getActivity()).sendBinAlertNotification(list);
                    Log.d("First Fragment Ln 155", sessionAdapter.getSessions().toString());
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

}