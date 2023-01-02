package com.opensource.ssu_ppy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.ocpsoft.prettytime.PrettyTime;

import io.reactivex.rxjava3.annotations.NonNull;

public class MeetChatAdapter extends FirestoreRecyclerAdapter<Chat,MeetChatAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    PrettyTime p = new PrettyTime();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MeetChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MeetChatAdapter.ViewHolder holder, int position, @NonNull Chat model) {
        holder.message.setText(model.getMessage());
        holder.name.setText(model.getName());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chat, parent, false);
            return new ViewHolder(view,viewType);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_meet_chat, parent, false);
            return new ViewHolder(view,viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getUid().equals(user.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, message;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if(viewType == MSG_TYPE_RIGHT) {
                name = itemView.findViewById(R.id.my_name);
                message = itemView.findViewById(R.id.my_chat_text);
            }
            else {
                name = itemView.findViewById(R.id.other_meet_name);
                message = itemView.findViewById(R.id.other_meet_chat_text);
            }
        }
    }
}
