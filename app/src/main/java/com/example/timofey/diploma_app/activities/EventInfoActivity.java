package com.example.timofey.diploma_app.activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timofey.diploma_app.data_classes.VKEvent;
import com.example.timofey.diploma_app.R;
import com.example.timofey.diploma_app.worked_classes.DateConverter;
import com.example.timofey.diploma_app.worked_classes.EventsImporter;

import java.io.IOException;

public class EventInfoActivity extends Activity {

    DownloadDrawableTask downloadDrawableTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_info);

        VKEvent event = (VKEvent) getIntent().getSerializableExtra("VKEvent");
        final TextView textView = (TextView) findViewById(R.id.eventInfo);
        textView.setMovementMethod(new ScrollingMovementMethod());


        if (event.getPhoto_200() != null) {
            downloadDrawableTask = new DownloadDrawableTask();
            downloadDrawableTask.execute(event.getPhoto_200());
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textView.append(event.getTitle() + "\n \n");

        textView.append("Описание: " + event.getDefinition() + "\n \n \n");
        DateConverter converter = new DateConverter(event.getDateStart());
        if (event.getPlace() != null){
            textView.append("Место проведения: " + event.getPlace() + "\n \n");
        }
        textView.append("Начало: " + converter.toInfoString() + '\n');
        converter.setUnixDate(event.getDateEnd());
        textView.append("Окончание: " + converter.toInfoString() + "\n \n");

        Button but = (Button) findViewById(R.id.button2);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Допустим, клик", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onPause() {
        downloadDrawableTask.cancel(true);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_info, menu);
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

    protected class DownloadDrawableTask extends AsyncTask<String, Void, Drawable>{

        @Override
        protected Drawable doInBackground(String... url) {
            Drawable drawable = null;
            try {
                drawable = EventsImporter.getDrawableFromUrl(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            TextView textView = (TextView) findViewById(R.id.eventInfo);

            if (drawable != null){
                drawable.setBounds(0, 0, 400, 400);

                textView.setCompoundDrawables(null, drawable, null, null);
            }
        }

        @Override
        protected void onCancelled() {

            super.onCancelled();
        }
    }
}

