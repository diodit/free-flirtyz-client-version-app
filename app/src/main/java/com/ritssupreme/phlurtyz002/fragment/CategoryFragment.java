package com.ritssupreme.phlurtyz002.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.GridView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.adapter.GridAdapter;
import com.ritssupreme.phlurtyz002.database.DatabaseHandler;
import com.ritssupreme.phlurtyz002.model.GridItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by kibrom on 3/20/17.
 */

public class CategoryFragment extends Fragment implements Checkable {

    private Context context;

    private MaterialFavoriteButton favoriteButton;

    private List<GridItem> selectedForFavorite = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        this.context = getContext();

        final List<String> itemNames = this.getArguments().getStringArrayList("itemNames");

        final List<String> imagePaths = this.getArguments().getStringArrayList("imagePaths");

        final List<Bitmap> images = new ArrayList<>();

        for (String imagePath : imagePaths) {

            try {

                InputStream is = context.openFileInput(imagePath);

                images.add(BitmapFactory.decodeStream(is));

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }

        }

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        GridAdapter adapter = new GridAdapter(getActivity(), itemNames, images);

        final GridView gridView = (GridView) view.findViewById(R.id.category_gridview);

        gridView.setAdapter(adapter);

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String fileName = imagePaths.get(position);

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

                GridItem item = new GridItem();

                item.setAssetName(itemNames.get(position));

                item.setAssetPath(imagePaths.get(position));

                DatabaseHandler db = new DatabaseHandler(context);

                db.addRecent(item);

            }
        });


        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                mode.setTitle("Select Emoji");

                mode.setSubtitle("One Emoji selected");

                MenuInflater inflater = getActivity().getMenuInflater();

                inflater.inflate(R.menu.action_mode, menu);


                return true;

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.add_to_favorite:

                        mode.finish();

                        DatabaseHandler db = new DatabaseHandler(context);

                        db.addFavoriteInBulk(selectedForFavorite);
                }

                int selectCount = gridView.getCheckedItemCount();

                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("One Emoji selected");


                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " Emoji selected");

                        break;
                }

                return true;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,long id, boolean checked) {

                //TODO add emojis in array. remove emoji from array when unchecked

                GridItem gridItem = new GridItem();

                gridItem.setAssetName(itemNames.get(position));

                gridItem.setAssetPath(imagePaths.get(position));

                if(checked){

                    selectedForFavorite.add(gridItem);

                }else {

                    Iterator<GridItem> it = selectedForFavorite.iterator();

                    while (it.hasNext()){

                        if(it.next().getAssetPath().equals(gridItem.getAssetPath())){

                            it.remove();
                        }
                    }
                }

                int selectCount = gridView.getCheckedItemCount();

                switch (selectCount) {

                    case 1:

                        mode.setSubtitle("Emoji selected");

                        break;

                    default:

                        mode.setSubtitle("" + selectCount + " Emoji selected");

                        break;
                }

            }
        });

      return view;
    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }
}




