package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private static final int EXISTING_BOOK =0 ;
    private Uri mCurrentBookUri;

    int quantity;
    int price;
    Button addButton;
    Button subButton;
    Button deleteButton;

    private boolean mBookEdited = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookEdited = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            addButton = findViewById(R.id.add_button);
            addButton.setVisibility(View.INVISIBLE);
            subButton = findViewById(R.id.minus_button);
            subButton.setVisibility(View.INVISIBLE);
            deleteButton = findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.INVISIBLE);

            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
        }
        mNameEditText = findViewById(R.id.book_name);
        mQuantityEditText = findViewById(R.id.book_quantity);
        mPriceEditText = findViewById(R.id.book_price);
        mSupplierEditText = findViewById(R.id.book_supplier);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone);

        getLoaderManager().initLoader(EXISTING_BOOK, null ,  this);

        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantityEditText.getText().toString());

                if (quantity >= 0 && quantity < 100) {
                    quantity = quantity + 1;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    getContentResolver().update(mCurrentBookUri, values, null, null);
                }
            }
        });

        subButton = findViewById(R.id.minus_button);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantityEditText.getText().toString());

                if (quantity > 0) {
                    quantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    getContentResolver().update(mCurrentBookUri, values, null, null);
                }
            }
        });

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmationDialog();
            }
        });

    }
    private void insertBook() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mSupplierPhoneEditText.getText().toString().trim();

        if (mCurrentBookUri == null && TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierString) || TextUtils.isEmpty(phoneString)) {
            missingInput();
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            missingInput();
        } else {
            quantity = Integer.parseInt(quantityString);
        }

        if (TextUtils.isEmpty(priceString)){
            missingInput();
        } else {
            price = Integer.parseInt(priceString);
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierString);
        values.put(InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, phoneString);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int affectedRows = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (affectedRows == 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor , menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertBook();
                return true;

            case R.id.action_delete:

                deleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!mBookEdited) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookEdited) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i,  Bundle bundle) {
        if (mCurrentBookUri == null) {
            return null;
        }

        String [] projection = {

                InventoryContract.BookEntry._ID,
                InventoryContract.BookEntry.COLUMN_BOOK_NAME,
                InventoryContract.BookEntry.COLUMN_BOOK_PRICE,
                InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY,
                InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE
        };
        return new CursorLoader(this, mCurrentBookUri ,projection ,null,null,null);

    }

    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int productName = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_NAME);
            int productPrice = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_PRICE);
            int productQuantity = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int suppName = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int suppNumber = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            String name = cursor.getString(productName);
            int price = cursor.getInt(productPrice);
            int quantity = cursor.getInt(productQuantity);
            String suppname = cursor.getString(suppName);
            String suppnumber = cursor.getString(suppNumber);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(suppname);
            mSupplierPhoneEditText.setText(suppnumber);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
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

    private void deleteProduct() {

        if (mCurrentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void missingInput() {
        Toast toast = Toast.makeText(this, getString(R.string.editor_insert_product_info_missing), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.show();
    }

}
