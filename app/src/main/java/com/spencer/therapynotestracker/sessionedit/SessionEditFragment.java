package com.spencer.therapynotestracker.sessionedit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.spencer.therapynotestracker.databinding.FragmentSessionEditBinding;

import java.io.IOException;
import java.io.OutputStream;

public class SessionEditFragment extends Fragment {

    private FragmentSessionEditBinding binding;

    private ActiveSessionViewModel activeSessionViewModel;


    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSessionEditBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_session_edit, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SessionEditFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        binding.buttonSaveSessionEdit.setOnClickListener(v -> {
                attemptEdit();
            }
        );

        binding.buttonSaveSessionDelete.setOnClickListener(v -> {
                attemptDelete();
            }
        );

        binding.buttonPrintSession.setOnClickListener(v -> {
            TextView date = getView().findViewById(R.id.view_date);
            EditText editTherapist = this.getView().findViewById(R.id.edit_therapist);
            String suggestedFileName = date.getText().toString() + editTherapist.getText().toString() + "session data.pdf";
            String mimeType = "application/pdf";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);

            someActivityResultLauncher.launch(intent);
        });

        activeSessionViewModel = new ViewModelProvider(requireActivity()).get(ActiveSessionViewModel.class);
        activeSessionViewModel.getSelectedItem().observe(getViewLifecycleOwner(), session -> {
            // Update the list UI.
            // Find the TextView by its ID
            TextView editDate = view.findViewById(R.id.view_date);
            EditText editAgenda = view.findViewById(R.id.edit_agenda);
            EditText editNotes = view.findViewById(R.id.edit_notes);
            EditText editTherapist = view.findViewById(R.id.edit_therapist);

            // Set the text
            editDate.setText(session.getDate());
            editAgenda.setText(session.getAgenda());
            editNotes.setText(session.getNotes());
            editTherapist.setText(session.getTherapist());
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        Uri fileUri = data.getData();

                        try {
                            OutputStream outputStream = getContext().getContentResolver().openOutputStream(fileUri);
                            if (outputStream != null) {
                                PdfDocument pdfDocument = PdfUtils.buildExportPDFString(getContext(), getView());

                                pdfDocument.writeTo(outputStream);

                                pdfDocument.close();

                                Toast.makeText(getContext(), "PDF file generated successfully.", Toast.LENGTH_SHORT).show();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle error during file writing
                            Toast.makeText(getContext(), "Failed to generate PDF file.", Toast.LENGTH_SHORT).show();
                        }

                        /// ////
                    }
                }
            });

    private void attemptEdit() {
        EditText editAgenda = getView().findViewById(R.id.edit_agenda);
        EditText editNotes = getView().findViewById(R.id.edit_notes);
        TextView date = getView().findViewById(R.id.view_date);
        EditText editTherapist = getView().findViewById(R.id.edit_therapist);

        String dateDate = date.getText().toString();
        String newAgenda = editAgenda.getText().toString();
        String newNotes = editNotes.getText().toString();
        String newTherapist = editTherapist.getText().toString();

        Session tempSesh = new Session(dateDate, newAgenda, newNotes, newTherapist);

        activeSessionViewModel.editItem(tempSesh);

        Toast.makeText(getView().getContext(), "Edited record for " + dateDate + " - " + editTherapist.getText(), Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(SessionEditFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    private void attemptDelete() {
        EditText editAgenda = getView().findViewById(R.id.edit_agenda);
        TextView date = getView().findViewById(R.id.view_date);
        String dateDate = date.getText().toString();

        activeSessionViewModel.deleteItem(dateDate);

        Toast.makeText(getView().getContext(), "Deleted the item for " + dateDate, Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(SessionEditFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}