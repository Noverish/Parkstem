package com.trams.parkstem;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noverish on 2016-07-07.
 */
public class ItemAdapter extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {
    private int mLayoutId;
    private int mGrabHandleId;
    private Drawable mainItemImage;
    private Drawable selected, notSelected;
    private ArrayList<String> selectedItemList;
    private boolean allSelected;

    public ItemAdapter(Context context, ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
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
        for(int i = 0;i<list.size();i++) {
            if(selectedItemList.contains(list.get(i).second)) {
                removeItem(i);
                i--;
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder>.ViewHolder {
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