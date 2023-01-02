package com.opensource.ssu_ppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OpenChatAdapter extends RecyclerView.Adapter<OpenChatAdapter.ViewHolder> {
    Context context;
    ArrayList<OpenChatItem> items = new ArrayList<OpenChatItem>();
    OnItemClickListener listener;
    OnItemLongClickListener listener1;

    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(ViewHolder holder, View view, int position);
    }

    public OpenChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.in_search_open_chat, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OpenChatItem item = items.get(position);
        holder.setItem(item);
        holder.setOnItemClickListener(listener);
        holder.setOnItemLongClickListener(listener1);
    }

    public void addItem(OpenChatItem item) {
        items.add(item);
    }

    public OpenChatItem getItem(int position) {
        return items.get(position);
    }

    public void clearItem() {
        items.clear();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OpenChatAdapter.OnItemLongClickListener listener1 ) {this.listener1=listener1;}

    static class ViewHolder extends RecyclerView.ViewHolder {
        OnItemClickListener listener;
        OnItemLongClickListener listener1;
        TextView openChatTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            openChatTitle = (TextView) itemView.findViewById(R.id.open_chat_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(listener1 != null) {
                        listener1.onItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }
        public void setItem(OpenChatItem item) {
            openChatTitle.setText(item.getName());
        }
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
        public void setOnItemLongClickListener(OnItemLongClickListener listener1){
            this.listener1 = listener1;
        }
    }
}
