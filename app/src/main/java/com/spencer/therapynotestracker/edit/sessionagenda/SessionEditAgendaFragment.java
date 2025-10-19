package com.spencer.therapynotestracker.edit.sessionagenda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.spencer.therapynotestracker.PdfUtils;
import com.spencer.therapynotestracker.R;
import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.databinding.FragmentSessionAgendaEditBinding;
import com.spencer.therapynotestracker.databinding.FragmentSessionEditBinding;
import com.spencer.therapynotestracker.edit.ActiveSessionViewModel;
import com.spencer.therapynotestracker.edit.session.SessionEditFragment;

import java.io.IOException;
import java.io.OutputStream;

public class SessionEditAgendaFragment extends Fragment {


    private FragmentSessionAgendaEditBinding binding;

    private ActiveSessionViewModel activeSessionViewModel;


    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSessionAgendaEditBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_session_agenda_edit, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSaveSessionEdit.setOnClickListener(v -> {
                    attemptEdit();
                }
        );

        activeSessionViewModel = new ViewModelProvider(requireActivity()).get(ActiveSessionViewModel.class);
        activeSessionViewModel.getSelectedItem().observe(getViewLifecycleOwner(), session -> {
            // Update the list UI.
            // Find the TextView by its ID
            EditText editAgenda = view.findViewById(R.id.edit_agenda);

            // Set the text
            editAgenda.setText(session.getAgenda());
        });
    }

    private void attemptEdit() {
        EditText editAgenda = getView().findViewById(R.id.edit_agenda);
        String date = activeSessionViewModel.getSelectedItem().getValue().getDate();

        String newAgenda = editAgenda.getText().toString();

        activeSessionViewModel.editItem(date, newAgenda);

        Toast.makeText(getView().getContext(), "Edited record for " + date, Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(SessionEditAgendaFragment.this)
                .navigate(R.id.action_SessionEditAgendaFragment_to_FirstFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
