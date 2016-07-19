package com.trams.parkstem.custom_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noverish on 2016-07-08.
 */
public class LocationChangeableListView extends LinearLayout {
    private Context context;
    private boolean inEditMode;
    private OnEditCompleteListener onEditCompleteListener;
    private OnItemRemovedListener onItemRemovedListener;

    private DragAdapter dragAdapter;
    private NormalAdapter normalAdapter;
    private ArrayList<Pair<Long, String>> listData;
    private LinearLayout normalMode, editMode;

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
        this.inEditMode = false;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.location_changeable_list_view, this);

        dragAdapter = new DragAdapter(context, listData, R.layout.location_changeable_list_item_editing, R.id.list_item_move_image, false);
        DragListView dragListView = (DragListView) findViewById(R.id.location_changeable_list_view_drag_list_view);
        dragListView.setAdapter(dragAdapter, false);
        dragListView.setLayoutManager(new LinearLayoutManager(context));
        dragListView.setCanDragHorizontally(false);

        LinearLayout selectAllButton = (LinearLayout) findViewById(R.id.location_changeable_list_view_select_all_button);
        selectAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dragAdapter.selectAll();
            }
        });

        LinearLayout deleteButton = (LinearLayout) findViewById(R.id.location_changeable_list_view_delete_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dragAdapter.removeSelectedItems();
            }
        });

        LinearLayout editButton = (LinearLayout) findViewById(R.id.location_changeable_list_view_edit_button);
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditMode();
            }
        });

        LinearLayout editCompleteButton = (LinearLayout) findViewById(R.id.location_changeable_list_view_edti_complete_button);
        editCompleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditMode();
                normalAdapter.setListData(dragAdapter.getItemList());
                if(onEditCompleteListener != null)
                    onEditCompleteListener.onEditCompleted(getMainItem());
            }
        });

        normalAdapter = new NormalAdapter(context, listData);
        ListView normalListView = (ListView) findViewById(R.id.location_changeable_list_view_normal_list_view);
        normalListView.setAdapter(normalAdapter);

        normalMode = (LinearLayout) findViewById(R.id.location_changeable_list_view_normal_mode);
        editMode = (LinearLayout) findViewById(R.id.location_changeable_list_view_edit_mode);
    }


    public void changeEditMode() {
        inEditMode = !inEditMode;
        if(inEditMode) {
            normalMode.setVisibility(INVISIBLE);
            editMode.setVisibility(VISIBLE);
        } else {
            normalMode.setVisibility(VISIBLE);
            editMode.setVisibility(INVISIBLE);
        }
    }


    public void setMainItemImage(Drawable mainItemImage) {
        dragAdapter.setMainItemImage(mainItemImage);
    }

    public void setListData(ArrayList<Pair<Long, String>> listData) {
        this.listData = listData;
        dragAdapter.setItemList(listData);
        normalAdapter.setListData(listData);

        //In start, all view is invisible. So when data added, change their visibility to visible
        if(!listData.isEmpty()) {
            inEditMode = true;
            changeEditMode();
        }
    }

    public Pair<Long, String> getMainItem() {
        if(listData.size() != 0)
            return listData.get(0);
        else
            return new Pair<>(0L, getResources().getString(R.string.action_car_register));
    }


    public void setOnEditCompleteListener(OnEditCompleteListener listener) {
        this.onEditCompleteListener = listener;
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }


    public class NormalAdapter extends BaseAdapter {
        private List<Pair<Long, String>> listData;
        LayoutInflater inflater;
        Context context;

        public NormalAdapter(Context context, List<Pair<Long, String>> listData) {
            this.listData = listData;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public String getItem(int position) {
            return listData.get(position).second;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.location_changeable_list_item_normal, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            String currentData = getItem(position);

            mViewHolder.text.setText(currentData);

            return convertView;
        }

        private class MyViewHolder {
            TextView text;

            public MyViewHolder(View item) {
                text = (TextView) item.findViewById(R.id.list_item_text_normal);
            }
        }

        public void setListData(List<Pair<Long, String>> listData) {
            this.listData = listData;
            notifyDataSetChanged();
        }
    }

    /**
     * Created by Noverish on 2016-07-07.
     */
    public class DragAdapter extends DragItemAdapter<Pair<Long, String>, DragAdapter.ViewHolder> {
        private int mLayoutId;
        private int mGrabHandleId;
        private Drawable mainItemImage;
        private Drawable selected, notSelected;
        private ArrayList<String> selectedItemList;
        private boolean allSelected;

        public DragAdapter(boolean dragOnLongPress) {
            super(dragOnLongPress);
        }

        public DragAdapter(Context context, ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
            super(dragOnLongPress);
            this.mLayoutId = layoutId;
            this.mGrabHandleId = grabHandleId;
            this.mainItemImage = new ColorDrawable(Color.TRANSPARENT);
            this.selectedItemList = new ArrayList<>();
            this.selected = ContextCompat.getDrawable(context, R.drawable.box_checkround);
            this.notSelected = ContextCompat.getDrawable(context, R.drawable.box_checkround_b);
            this.allSelected = false;
            setHasStableIds(true);
            setItemList(list);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            String text = mItemList.get(position).second;
            holder.textView.setText(text);
            if(position == 0)
                holder.mainItem.setImageDrawable(mainItemImage);
            else
                holder.mainItem.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            if(selectedItemList.contains(text))
                holder.selectedItem.setImageDrawable(selected);
            else
                holder.selectedItem.setImageDrawable(notSelected);
        }

        @Override
        public long getItemId(int position) {
            return mItemList.get(position).first;
        }

        public void setMainItemImage(Drawable drawable) {
            mainItemImage = drawable;
        }

        public void selectAll() {
            selectedItemList.clear();
            if(!allSelected) {
                List<Pair<Long, String>> list = getItemList();
                for (Pair<Long, String> item : list) {
                    selectedItemList.add(item.second);
                }
            }
            allSelected = !allSelected;
            notifyDataSetChanged();
        }

        public void removeSelectedItems() {
            List<Pair<Long, String>> list = getItemList();
            if(onItemRemovedListener != null)
                onItemRemovedListener.onItemRemoved(list);
            for(int i = 0;i<list.size();i++) {
                if(selectedItemList.contains(list.get(i).second)) {
                    removeItem(i);
                    i--;
                }
            }
            notifyDataSetChanged();
        }

        public class ViewHolder extends DragItemAdapter<Pair<Long, String>, ViewHolder>.ViewHolder {
            public TextView textView;
            public ImageView mainItem;
            public ImageView selectedItem;

            public ViewHolder(final View itemView) {
                super(itemView, mGrabHandleId);
                textView = (TextView) itemView.findViewById(R.id.list_item_text);
                mainItem = (ImageView) itemView.findViewById(R.id.list_item_main_item);
                selectedItem = (ImageView) itemView.findViewById(R.id.list_item_select);
            }

            @Override
            public void onItemClicked(View view) {
                String text = textView.getText().toString();
                if(selectedItemList.contains(text))
                    selectedItemList.remove(text);
                else
                    selectedItemList.add(text);
                notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClicked(View view) {
                Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }


    public interface OnEditCompleteListener {
        void onEditCompleted(Pair<Long, String> mainItem);
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(List<Pair<Long, String>> removeItemList);
    }
}





/*
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
        }
    }
});

 */