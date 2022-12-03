package com.ritssupreme.phlurtyz002.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.api.models.AllCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AllCategory> imageObjects;

    private LayoutInflater mLayoutInflate;


    public MyGridAdapter(Context context, ArrayList<AllCategory> imageObjects) {
        this.context = context;
        this.imageObjects = imageObjects;

        this.mLayoutInflate = LayoutInflater.from(context);
    }

    public int getCount() {
        if (imageObjects != null) return imageObjects.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (imageObjects != null && imageObjects.size() > position)
            return imageObjects.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        if (imageObjects != null && imageObjects.size() > position)
            return imageObjects.get(position).getId();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();

            convertView = mLayoutInflate.inflate(R.layout.grid_view_category, parent,
                    false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.categoryImage);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.categoryImagelabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AllCategory imageObject = (AllCategory) getItem(position);
        if (imageObject != null) {
//            Glide
//                    .with(context)
//                    .load(imageObject.getImage())
//                    .centerCrop()
//                    .crossFade()
//                    .into(viewHolder.imageView);
//            Picasso.with(context)
//                    .load(imageObject.getFile())
////                    .resize(100,100)
//                    .into(viewHolder.imageView);

            Glide.with(context)
                    .load(imageObject.getFile())
                    .into(viewHolder.imageView);


            if(viewHolder.textView.getVisibility() == View.VISIBLE) {
                viewHolder.textView.setText(imageObject.getName());
            }
        }



        return convertView;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
