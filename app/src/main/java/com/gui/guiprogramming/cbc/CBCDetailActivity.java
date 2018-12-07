package com.gui.guiprogramming.cbc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.cbc.cbc_model.CBCNewsStory;

/**
 * CBCDetailActivity receives an instance of CBCNewsStory class,
 * through intent, which will help to display the article info to
 * the UI.
 * */
public class CBCDetailActivity extends Activity {

    String articleLink = "";
    String title;
    Button btnSave, btnViewMore;
    CBCDBManager db;
    CBCNewsStory data;
    boolean intentReceived = false;
    LinearLayout linearParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        TextView tvNewsTitle = findViewById(R.id.tvNewsTitle);
        TextView tvNewsDesc = findViewById(R.id.tvNewsDesc);
        TextView tvAuthName = findViewById(R.id.tvAuthName);
        TextView tvNewsPubDate = findViewById(R.id.tvNewsPubDate);
        TextView tvNewsCategory = findViewById(R.id.tvNewsCategory);
        btnViewMore = findViewById(R.id.btnViewMore);
        btnSave = findViewById(R.id.btnSave);
        linearParent = findViewById(R.id.linearParent);
        Intent intent = getIntent();
        //safe checking - for preventing NullPointerException
        if (intent.hasExtra("DATA")) {
            intentReceived = true;
            data = (CBCNewsStory) intent.getSerializableExtra("DATA");
            title = data.getTitle();
            String desc = data.getDescription();
            String pubDate = data.getPubDate();
            String author = data.getAuthor();
            String category = data.getCategory();
            articleLink = data.getLink();
            tvNewsTitle.setText(title);
            tvNewsDesc.setText(desc);
            tvNewsPubDate.setText(pubDate);
            tvAuthName.setText(author);
            tvNewsCategory.setText(category);
            db = new CBCDBManager(CBCDetailActivity.this);
            db.open();
            isExists(db);
        } else
            data = new CBCNewsStory();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentReceived) {
                    String message = "";
                    //if data already exists, then delete otherwise insert data
                    if (isExists(db)) {
                        //DELETE
                        if (db.removeArticle(title)) {
                            setResult(1);
                            message = getResources().getString(R.string.cbc_snack_removed);
                        } else {
                            message = getResources().getString(R.string.cbc_snack_removed_fail);
                        }
                    } else {
                        //INSERT
                        if (db.saveArticle(data) != -1) {
                            message = getResources().getString(R.string.cbc_snack_add);
                        } else {
                            message = getResources().getString(R.string.cbc_snack_add_fail);
                        }
                    }
                    //checking again for existence to update a button text.
                    isExists(db);
                    //showing Snackbar
                    Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opening a system browser with article URL
                Intent i = new Intent(Intent.ACTION_VIEW);
                //setting URL to intent
                i.setData(Uri.parse(articleLink));
                startActivity(i);
            }
        });
    }

    /**
     * <p>Check for data into database,
     * and update the button text.</p>
     *
     * @return true if data exists otherwise false.
     * @see CBCDBManager#isExists(String)
     */
    boolean isExists(CBCDBManager manager) {
        boolean isExists = manager.isExists(title);
        if (isExists) {
            String removeText = getResources().getString(R.string.cbc_remove_offline_article);
            btnSave.setText(removeText);
        } else {
            String saveText = getResources().getString(R.string.cbc_save_article_offline);
            btnSave.setText(saveText);
        }
        return isExists;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //closing database connection
        db.close();
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
                startActivity(new Intent(CBCDetailActivity.this, CBCStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(CBCDetailActivity.this, CBCFavoritesActivity.class));
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
        final Dialog dialog = new Dialog(CBCDetailActivity.this);
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
}
