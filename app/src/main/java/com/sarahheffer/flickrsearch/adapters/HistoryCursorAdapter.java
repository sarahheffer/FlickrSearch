package com.sarahheffer.flickrsearch.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarahheffer.flickrsearch.R;

import java.util.List;

public class HistoryCursorAdapter extends CursorAdapter {

    private List<String> historyItems;

    private TextView suggestionText;

    public HistoryCursorAdapter(Context context, Cursor cursor, List<String> items) {
        super(context, cursor, false);
        this.historyItems = items;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        suggestionText.setText(historyItems.get(cursor.getPosition()));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_search_suggestion, parent, false);
        suggestionText = (TextView) view.findViewById(R.id.suggestionText);
        return view;
    }
}