package com.spencer.therapynotestracker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.databinding.ActivityMainBinding;
import com.spencer.therapynotestracker.sessionlist.SessionListViewModel;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CREATE_FILE_REQUEST_CODE = 1;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SessionListViewModel sessionListViewModel;

    private final int REQUEST_POST_NOTIFICATION_STATE_PERMISSION = 1;

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
        }
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