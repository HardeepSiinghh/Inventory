package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;

class BookCursorAdapter extends CursorAdapter{
    public BookCursorAdapter(Context context , Cursor c){
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent , false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);

        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(InventoryContract.BookEntry._ID));
        int nameColumnIndex =cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY);

        Log.v("Initial Quantity","=" + quantityColumnIndex);

        String bookName =cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(bookName);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);

        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri saleUri = ContentUris.withAppendedId(InventoryContract.BookEntry.CONTENT_URI ,idColumnIndex);
                Log.v("SaleUri","= " + saleUri);
                int quantity = Integer.parseInt(quantityTextView.getText().toString());

                Log.v("Numbers", " " + quantity + " " + quantity);
                if (quantity > 0) {
                    quantity = quantity - 1;

                    Log.v("Update", " " + quantity + " " + quantity);

                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    context.getContentResolver().update(saleUri, values, null, null);
                }
            }
        });
    }
}
