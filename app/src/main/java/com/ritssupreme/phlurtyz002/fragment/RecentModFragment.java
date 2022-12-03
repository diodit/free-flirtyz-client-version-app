package com.ritssupreme.phlurtyz002.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.GridAdapter;
import com.ritssupreme.phlurtyz002.database.DatabaseHandler;
import com.ritssupreme.phlurtyz002.model.GridItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by kibrom on 3/20/17.
 */

public class RecentModFragment extends Fragment {

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        this.context = getContext();


        View view = inflater.inflate(R.layout.fragment_recent_mod, container, false);

        DatabaseHandler db = new DatabaseHandler(context);

        final Stack<GridItem> items = db.getRecentList();
        final Stack<GridItem> itemsUntouched = items;

        List<String> itemNames = new ArrayList<>();

        List<Bitmap> images = new ArrayList<>();

        for (GridItem item : items) {

            try {

                InputStream is = context.openFileInput(item.getAssetPath());

                images.add(BitmapFactory.decodeStream(is));

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }

            itemNames.add(item.getAssetName());
        }

        GridView gridView = (GridView) view.findViewById(R.id.recent_mod_gridview);

        GridAdapter adapter = new GridAdapter(getActivity(), itemNames, images);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String fileName = items.get(position).getAssetPath();

                final File emojiFile = new File(context.getFilesDir(), fileName);

                Uri uri = FileProvider.getUriForFile(context, "com.dio.phlurtyz002", emojiFile);

                Intent sendIntent = ShareCompat.IntentBuilder.from(getActivity())

                        .setType("image/png")

                        .setSubject("Subject")

                        .setStream(uri)

                        .setChooserTitle("Share Emoji")

                        .createChooserIntent()

                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(sendIntent);

            }
        });


      return view;
    }
}




