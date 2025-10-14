package com.spencer.therapynotestracker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.databinding.ActivityMainBinding;
import com.spencer.therapynotestracker.sessionlist.SessionListViewModel;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CREATE_FILE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_PICK_FILE = 2;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SessionListViewModel sessionListViewModel;

    private final int REQUEST_POST_NOTIFICATION_STATE_PERMISSION = 1;

    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NotificationChannelHelper.createNotificationChannel(this);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        sessionListViewModel = new ViewModelProvider(this).get(SessionListViewModel.class);

        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "There's nothing to setup", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_export_data) {
            Toast.makeText(MainActivity.this, "Exporting data!", Toast.LENGTH_SHORT).show();
            String suggestedFileName = "session data.txt";
            String mimeType = "text/plain";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);

            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
        } else if (id == R.id.action_import_data) {
            //Toast.makeText(MainActivity.this, "Importing data!", Toast.LENGTH_SHORT).show();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                Toast.makeText(MainActivity.this, "Already have permission to import data", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain"); // Set the MIME type to allow all file types
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE_PICK_FILE);

        }

        return super.onOptionsItemSelected(item);
    }

    private String buildExportDataString() {
        List<Session> sessions = sessionListViewModel.getSessions().getValue();

        StringBuilder result = new StringBuilder();

        for (Session session : sessions) {
            result.append(session.toString());
            result.append("\n");
        }

        Log.d("MainActivity", result.toString());

        return result.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    // Now, write your generated file content to this URI
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
                        if (outputStream != null) {
                            // Example: writing a simple string
                            String content = buildExportDataString();
                            outputStream.write(content.getBytes());
                            outputStream.close();
                            // File saved successfully
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle error during file writing
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                // Process the selected file using its URI
                Log.d("Main Activity 152", fileUri.toString());
                try {
                    extractDataFromFile(fileUri);
                } catch (IOException | CsvValidationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void extractDataFromFile(Uri fileUri) throws IOException, CsvValidationException {

        FileUtils fileUtils = new FileUtils(getBaseContext());
        String result = fileUtils.getPath(fileUri);
        File csvFile = new File(result);

        CSVReader csvReader = new CSVReader(new FileReader(csvFile));
        String[] nextLine;
        StringBuilder columns = new StringBuilder();

        int deleteWasRun = 0;

        String tempSesh = "";
        Session tempSession = new Session();

        while ((nextLine = csvReader.readNext()) != null) {
            if (deleteWasRun == 0) {
                if (sessionListViewModel.getSessions().getValue() != null && !sessionListViewModel.getSessions().getValue().isEmpty())
                    sessionListViewModel.deleteAll();
                deleteWasRun =1;
            }
            // nextLine[] is an array of values from the line
            for (int i = 0; i < nextLine.length; i++) {
                if (i == nextLine.length - 1) {
                    columns.append(nextLine[i]).append("\n");

                    tempSesh = columns.toString();
                    tempSession = transformCSVtoSession(tempSesh);

                    sessionListViewModel.insert(tempSession);
                    columns.delete(0, columns.length());
                } else {
                    columns.append(nextLine[i]).append(",");
                }
            }
        }

        String csvContent = columns.toString();

        Log.d("Main Activity 190", csvContent);

        return columns.toString();
    }

    private Session transformCSVtoSession(String tempSesh) {
        Session tempResult = new Session();

        String finalChars = tempSesh.substring(tempSesh.length()-1, tempSesh.length());

        if (StringUtils.equals(finalChars, "\n")) {
            tempSesh = tempSesh.substring(0, tempSesh.length()-1);
        }

        int i = 0;
        int nextCommaIndex = tempSesh.indexOf(",");
        tempResult.setDate(tempSesh.substring(i, nextCommaIndex));

        i = nextCommaIndex+1;
        nextCommaIndex = tempSesh.indexOf(",", i);
        tempResult.setAgenda(tempSesh.substring(i, nextCommaIndex));

        i = nextCommaIndex+1;
        nextCommaIndex = tempSesh.indexOf(",", i);
        tempResult.setNotes(tempSesh.substring(i, nextCommaIndex));

        i = nextCommaIndex+1;
        tempResult.setTherapist(tempSesh.substring(i));

        return tempResult;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void sendBinAlertNotification(List<Session> bins) {

        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(this, AlertDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_POST_NOTIFICATION_STATE_PERMISSION, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "BIN_ALERT_CHANNEL")
                .setChannelId(NotificationChannelHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_alert))
                .setContentTitle("Adding Numbers")
                .setContentText("We're adding numbers!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ic_alert))
                        .setBigContentTitle("This is Big Notification"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.POST_NOTIFICATIONS)) {
                showExplanation();
            } else {
                requestPermission(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            Toast.makeText(MainActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }
        NotificationManagerCompat.from(this).notify(REQUEST_POST_NOTIFICATION_STATE_PERMISSION, builder.build());

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_POST_NOTIFICATION_STATE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Needed")
                .setMessage("Rationale")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(Manifest.permission.READ_PHONE_STATE);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, 1);
    }

}