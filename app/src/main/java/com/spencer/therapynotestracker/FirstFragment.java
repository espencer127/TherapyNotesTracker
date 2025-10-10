package com.spencer.therapynotestracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spencer.therapynotestracker.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements View.OnClickListener {

    private FragmentFirstBinding binding;

    private List<SessionModel> list;

    private HomeViewModel homeViewModel;

    private NoteAdapter listAdapter;
    FloatingActionButton enterButton;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        this.list = new ArrayList<>();

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_first, container, false);
        ListView listView = contentView.findViewById(R.id.listview);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.setBins(list);

        this.listAdapter = new NoteAdapter(homeViewModel.getBins().getValue(), container.getContext());
        listView.setAdapter(listAdapter);

        enterButton = contentView.findViewById(R.id.fab);
        enterButton.setOnClickListener(this);

        return contentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe the LiveData. The onChanged() method is called when the data changes.
        homeViewModel.getBins().observe(getViewLifecycleOwner(), bins -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (homeViewModel.getBins().getValue().size() > 0)
                    ((MainActivity) getActivity()).sendBinAlertNotification(bins);
            }

            listAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            List<SessionModel> newList = new ArrayList<>();

            //prompt for item
            promptForString();

            homeViewModel.addItems(newList);

            Toast.makeText(v.getContext(), "Add Button Clicked!", Toast.LENGTH_SHORT).show();
            // Add more cases for other buttons if needed
        }
    }

    private void promptForString() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Enter alert quantity");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogueView = inflater.inflate(R.layout.alert_session_prompt, null);
        builder.setView(dialogueView);

        final EditText alertPromptDate = dialogueView.findViewById(R.id.alertSessionDate);
        final EditText alertPromptAgenda = dialogueView.findViewById(R.id.alertSessionAgenda);
        final EditText alertPromptNotes = dialogueView.findViewById(R.id.alertSessionNotes);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionModel tempSession = new SessionModel();
                tempSession.setDate(alertPromptDate.getText().toString());
                tempSession.setAgenda(alertPromptAgenda.getText().toString());
                tempSession.setNotes(alertPromptNotes.getText().toString());
                list.add(tempSession);
                listAdapter.notifyDataSetChanged();
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

}