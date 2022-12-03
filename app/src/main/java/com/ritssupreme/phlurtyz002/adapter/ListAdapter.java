package com.ritssupreme.phlurtyz002.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ritssupreme.phlurtyz002.R;

import java.util.List;

/**
 * Created by kibrom on 5/29/17.
 */

public class ListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private final String[] itemname;

    private final Bitmap[] image;

    @Override
    public int getCount() {

        return image.length;
    }

    public ListAdapter(Activity context, List<String> itemname, List<Bitmap> image) {

        super(context, R.layout.list_category, itemname);

        this.context = context;

        this.itemname = itemname.toArray(new String[0]);

        this.image = image.toArray(new Bitmap[0]);
    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.list_category, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.label);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);

        txtTitle.setText(itemname[position]);

        imageView.setImageBitmap(image[position]);

        return rowView;

    }

}

