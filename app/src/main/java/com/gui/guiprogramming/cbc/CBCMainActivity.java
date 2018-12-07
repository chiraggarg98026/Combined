package com.gui.guiprogramming.cbc;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.cbc.cbc_adapter.CBCNewsListAdapter;
import com.gui.guiprogramming.cbc.cbc_model.CBCNewsStory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * CBCMainActivity is launcher activity for CBC-NEWS-READER app
 * */
public class CBCMainActivity extends Activity {

    ListView lvMovies;
    //Custom ListAdapter class
    CBCNewsListAdapter listAdapter;
    //Model - getter-setter class to hold data for eac row of a list
    ArrayList<CBCNewsStory> mRowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        lvMovies = findViewById(R.id.lvMovies);

        mRowData = new ArrayList<>();
        listAdapter = new CBCNewsListAdapter(CBCMainActivity.this, mRowData);
        lvMovies.setAdapter(listAdapter);
        new Async().execute();

        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(CBCMainActivity.this, CBCDetailActivity.class)
                        .putExtra("DATA", mRowData.get(i)));
            }
        });
    }

    /**
     * Binding menu to activity
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handling click event of menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuStat:
                startActivity(new Intent(CBCMainActivity.this, CBCStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(CBCMainActivity.this, CBCFavoritesActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }


    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(CBCMainActivity.this);
        //We do not need title for custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting layout to dialog
        dialog.setContentView(R.layout.cbc_layout_help_dialog);
        //initializing widgets of custom dialog layout
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //showing dialog with MATCH_PARENT width.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }

    //Async Task to fetch the live RSS data
    public class Async extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(CBCMainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Displaying a progress dialog to user
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://www.cbc.ca/cmlink/rss-world");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();
                mRowData.clear();
                CBCNewsStory data = null;
                //parsing the XML response
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        Log.i("Story scanning tag", xpp.getName());
                        if (xpp.getName().equals("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equals("title")) {
                            if (insideItem) {
                                data = new CBCNewsStory();
                                String title = xpp.nextText();
                                Log.i("Story title==>", title);
                                data.setTitle(title);
                            }
                        } else if (xpp.getName().equals("link")) {
                            if (insideItem) {
                                String link = xpp.nextText();
                                Log.i("Story Link==>", link);
                                if (data != null)
                                    data.setLink(link);
                            }
                        } else if (xpp.getName().equals("description")) {
                            if (insideItem) {
                                String d = xpp.nextText();
                                Log.i("Story description==>", d.trim());
                                String description = d.substring(d.indexOf("<p>"), d.lastIndexOf("</p>"));
                                description = description.replace("<p>", "");
                                Log.i("Story description==>", "from P tag:" + d);
                                if (data != null)
                                    data.setDescription(description);
                            }
                        }else if (xpp.getName().equals("pubDate")) {
                            if (insideItem) {
                                String pubDate = xpp.nextText();
                                Log.i("pubDate==>", pubDate);
                                if (data != null)
                                    data.setPubDate(pubDate);
                            }
                        }else if (xpp.getName().equals("author")) {
                            if (insideItem) {
                                String author = xpp.nextText();
                                Log.i("author==>", author);
                                if (data != null)
                                    data.setAuthor(author);
                            }
                        }else if (xpp.getName().equals("category")) {
                            if (insideItem) {
                                String category = xpp.nextText();
                                Log.i("category==>", category);
                                if (data != null)
                                    data.setCategory(category);
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("item")) {
                        insideItem = false;
                        mRowData.add(data);
                    }
                    eventType = xpp.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            //updating the listview
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     * To reading the response of URL
     * @param url url of an API
     * @return null if failed to get inputStream, otherwise InputStream for a URL
     * */
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

}
