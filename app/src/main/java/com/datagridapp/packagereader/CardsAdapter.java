package com.datagridapp.packagereader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardsAdapter extends CardScrollAdapter {

    private List<Card> mCards;
    private Context mContext;

    public CardsAdapter(Context context) {
        mContext = context;
    }

    public void setCards(String[] content) {
        mCards = new ArrayList<Card>(content.length);

        for (String text : content) {
            Card card = new Card(mContext);
            card.setText(text);
            card.setFootnote("");
            mCards.add(card);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getPosition(Object item) {
        return mCards.indexOf(item);
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Override
    public Object getItem(int position) {
        return mCards.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return Card.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position){
        return mCards.get(position).getItemViewType();
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        return  mCards.get(position).getView(convertView, parent);
    }
}
