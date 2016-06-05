package com.example.timofey.diploma_app.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.timofey.diploma_app.data_classes.VKEvent;
import com.example.timofey.diploma_app.worked_classes.CustomListAdapter;
import com.example.timofey.diploma_app.worked_classes.EventsImporter;
import com.example.timofey.diploma_app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.Manifest;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private String[] scope = new String[]{VKScope.GROUPS};


    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new JacksonFactory();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };
    GoogleAccountCredential credential;

    ProgressDialog mProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
//            @Override
//            public void initialize(HttpRequest request) throws IOException {
//                request.getHeaders().setAuthorization()
//            }
//        }
        VKSdk.login(this, scope);
        credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
// Пользователь успешно авторизовался
                ListAdapter listAdapter;
                final ArrayList<Object> events = new ArrayList<Object>();


                final VKRequest request = VKApi.groups().get(VKParameters.from(VKApiConst.EXTENDED,
                        1, VKApiConst.FILTERS, "event", VKApiConst.FIELDS, "place,description,start_date,finish_date"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        Toast.makeText(getApplicationContext(), "request выполнен", Toast.LENGTH_SHORT).show();
                        ArrayList<VKEvent> events = new ArrayList<VKEvent>();


                        TextView textView = (TextView) findViewById(R.id.textView);

                        EventsImporter eventsImporter = new EventsImporter();

                        try {
                            events = eventsImporter.getEventsByJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ListView listView = (ListView) findViewById(R.id.listView);

                        Collections.sort(events, new Comparator<VKEvent>() {
                            @Override
                            public int compare(VKEvent lhs, VKEvent rhs) {
                                return lhs.compareTo(rhs);
                            }
                        });
                        CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, events);
                        listView.setAdapter(adapter);

                        final Intent intent = new Intent(MainActivity.this, EventInfoActivity.class);
                        //обработка списка
                        final ArrayList<VKEvent> finalEvents = events;
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                intent.putExtra("VKEvent", finalEvents.get(position));
                                startActivity(intent);
                            }
                        });



                        if (events.size() == 0){
                            textView.setText("Не найдено ни одного действующего мероприятия");
                        } else {
                            textView.append(String.valueOf(events.size()));
                        }


                        Button but = (Button) findViewById(R.id.button);
                        but.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               // Calendar service = new Calendar.Builder(transport, jsonFactory, );
                            //    Event createdEvent = service.Events.Insert("primary", finalEvents.get(1)).execute;
                                new MakeRequestTask(credential).execute();
                                Toast.makeText(getApplicationContext(), "Допустим, клик", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                Toast.makeText(getApplicationContext(), "Вы успешно авторизовались", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), "Произошла ошибка авторизации", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //part of code from googleCalendar quickstart
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(){
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS)){
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null){
                credential.setSelectedAccountName(accountName);
                getResultsFromApi();
            }
        }
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(getApplicationContext(), "Пожалуйста, подключитесь к сети и перезапустите приложение.", Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(credential).execute();
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    @Override
    public void onPermissionsGranted(int i, List<String> list) {

    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {

    }


    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {

        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            System.out.println("task запущен");
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Calendar.Builder(transport, jsonFactory, credential)
                    .setApplicationName("applicationName").build();

            System.out.println("Календарь создан");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("doInBackground выполняется");
                insertTheEvent();
                System.out.println("получилось");
            } catch (Exception e) {
                mLastError = e;
                System.out.println(e.toString());
                cancel(true);
            }
            return null;
        }

        private void insertTheEvent() throws IOException {

            System.out.println("Вошли в метод");

            //проба
            Event event = new Event()
                    .setSummary("Google I/O 2015")
                    .setLocation("800 Howard St., San Francisco, CA 94103")
                    .setDescription("A chance to hear more about Google's developer products.");

            DateTime startDateTime = new DateTime("2016-06-28T09:00:00-07:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("America/Los_Angeles");
            event.setStart(start);

            DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("America/Los_Angeles");
            event.setEnd(end);

            String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
            event.setRecurrence(Arrays.asList(recurrence));

            EventAttendee[] attendees = new EventAttendee[] {
                    new EventAttendee().setEmail("lpage@example.com"),
                    new EventAttendee().setEmail("sbrin@example.com"),
            };
            event.setAttendees(Arrays.asList(attendees));

            EventReminder[] reminderOverrides = new EventReminder[] {
                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                    new EventReminder().setMethod("popup").setMinutes(10),
            };
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);

            String calendarId = "primary";

            System.out.println(mService.events().toString());

            // Проблема тутЪ
            event = mService.events().insert(calendarId, event).execute();


            System.out.println("Event created: %s\n" + event.getHtmlLink());

        }

        @Override
        protected void onPreExecute() {
//            mProgress.show();
        }


        @Override
        protected void onCancelled() {
//            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getApplicationContext(),"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


