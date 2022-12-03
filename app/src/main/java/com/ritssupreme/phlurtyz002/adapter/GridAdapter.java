package com.ritssupreme.phlurtyz002.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ritssupreme.phlurtyz002.R;

import java.util.List;

/**
 * Created by kibrom on 5/29/17.
 */

public class GridAdapter extends BaseAdapter {

    private final Context context;

    private final String[] itemname;

    private final Bitmap[] image;

    public GridAdapter(Activity context, List<String> itemname, List<Bitmap> image) {

        this.context = context;

        this.itemname = itemname.toArray(new String[0]);

        this.image = image.toArray(new Bitmap[0]);
    }


    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.grid_view_category, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.categoryImagelabel);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.categoryImage);

        if(txtTitle.getVisibility() == View.VISIBLE){
            txtTitle.setText(itemname[position]);
        }


        imageView.setImageBitmap(image[position]);

        return rowView;

    }

    @Override
    public int getCount() {

        return image.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
