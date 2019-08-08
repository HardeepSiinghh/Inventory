package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.inventory.data.InventoryContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Button button = findViewById(R.id.edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this , EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(InventoryContract.BookEntry.CONTENT_URI , id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER , null , this);

    }
    private void insertBook(){
        ContentValues values = new ContentValues();
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_NAME, "You Don't know JS");
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_PRICE, 4);
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY, 10);
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Kylee Simpson");
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE , "999-322-7633");

        Uri newUri = getContentResolver().insert(InventoryContract.BookEntry.CONTENT_URI, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertBook();
                return true;

            case R.id.action_delete_all_entries:

                deleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id,  Bundle bundle) {
        String[] projection = {
                InventoryContract.BookEntry._ID,
                InventoryContract.BookEntry.COLUMN_BOOK_NAME,
                InventoryContract.BookEntry.COLUMN_BOOK_PRICE,
                InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY
        };

        return new CursorLoader(this , InventoryContract.BookEntry.CONTENT_URI, projection ,
                null ,null ,null);
    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    private void deleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteAllProduct() {
        int rowsdeleted = getContentResolver().delete(InventoryContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsdeleted + " rows deleted from the product database");
    }
}
