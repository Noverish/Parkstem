package com.trams.parkstem.custom_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.trams.parkstem.ItemAdapter;
import com.trams.parkstem.R;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

/**
 * Created by Noverish on 2016-07-08.
 */
public class LocationChangeableListView extends LinearLayout {
    private Context context;

    private DragListView dragListView;
    private ArrayList<Pair<Long, String>> listData;
    private ItemAdapter listAdapter;

    public LocationChangeableListView(Context context) {
        super(context);

        if(!isInEditMode())
            init(context);
    }

    public LocationChangeableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(!isInEditMode())
            init(context);
    }

    private void init(Context contextParam) {
        this.context = contextParam;
        this.listData = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.location_changeable_list_view, this);

        dragListView = (DragListView) findViewById(R.id.location_changeable_list_view_drag_list_view);
        dragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
                //Toast.makeText(context, "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    //Toast.makeText(context, "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                    for(int i = 0;i<listData.size();i++)
                        Log.e("onItemDragEnded", listData.get(i).first + " - " + listData.get(i).second);
                }
            }
        });

        dragListView.setLayoutManager(new LinearLayoutManager(context));
        listAdapter = new ItemAdapter(context, listData, R.layout.location_changeable_list_view_item, R.id.list_item_move_image, false);
        dragListView.setAdapter(listAdapter, false);
        dragListView.setCanDragHorizontally(false);

        LinearLayout selectAllButton = (LinearLayout) findViewById(R.id.location_changeable_list_view_select_all_button);
        selectAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.selectAll();
            }
        });

        LinearLayout deleteButton = (LinearLayout) findViewById(R.id.location_changeable_list_view_delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.removeSelectedItems();
            }
        });
    }

    private void refresh() {

    }

    public void setMainItemImage(Drawable mainItemImage) {
        listAdapter.setMainItemImage(mainItemImage);
        refresh();
    }

    public void setListData(ArrayList<Pair<Long, String>> listData) {
        listAdapter.setItemList(listData);
        refresh();
    }
}
