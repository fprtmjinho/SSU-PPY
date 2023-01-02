package com.opensource.ssu_ppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {
    Context context;
    ArrayList<String> items = new ArrayList<String>();
    OnItemClickListener listener;
    OnItemLongClickListener listener1;
    HashMap<String,Object> meetingItems = new HashMap<>();
    ArrayList<String> num = new ArrayList<String>();
    ArrayList<String> id = new ArrayList<String>();


    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(ViewHolder holder, View view, int position);
    }

    public MeetingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.in_search_meeting, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.setItem(item);
        holder.setOnItemClickListener(listener);
        holder.setOnItemLongClickListener(listener1);
    }
    //뷰 클릭시 띄울 다이얼로그 뷰에 필요한 정보 저장
    public void putItem(HashMap<String,Object> meetingItem) { meetingItems.putAll(meetingItem);}

    public Object popItem(String key){ return meetingItems.get(key); }

    public void addNum(String roomNum){num.add(roomNum);}

    public String getNum(int position) { return num.get(position);}

    public void addOtherId(String roomId){id.add(roomId);}

    public String getOtherId(int position) { return id.get(position);}

    public void addItem(String roomName) {
        items.add(roomName);
    }

    public String getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener1 ) {this.listener1=listener1;}

    static class ViewHolder extends RecyclerView.ViewHolder {
        OnItemClickListener listener;
        OnItemLongClickListener listener1;
        TextView meetingName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            meetingName = (TextView) itemView.findViewById(R.id.meetingName);

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
        public void setItem(String roomName) { meetingName.setText(roomName); }
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
        public void setOnItemLongClickListener(OnItemLongClickListener listener1){
            this.listener1 = listener1;
        }
    }
}
