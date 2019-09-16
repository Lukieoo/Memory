package com.anioncode.memory.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyLog;
import com.anioncode.memory.Models.Friend;

import com.anioncode.memory.Models.Places;
import com.anioncode.memory.Models.User;
import com.anioncode.memory.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_adapter extends RecyclerView.Adapter<Profile_adapter.ExampleViewHolder> {

    public ArrayList<Friend> friends = new ArrayList();
    private Profile_adapter.OnItemClickListener mListener;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference docRef = db.collection("USERS");
    private CollectionReference noteRef = db.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("FRIENDS");
    //  CollectionReference docRef = db.collection("USERS");
    public static String AVATAR = "";

    public Profile_adapter(Context mContext, ArrayList<Friend> friends, Profile_adapter.OnItemClickListener mListener) {
        this.friends = friends;
        this.mListener = mListener;
        this.mContext = mContext;
    }


    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        final Friend currentItem = friends.get(position);


//        String name = currentItem.getFriend_Username().toString();
        final String id = currentItem.getFriend_id().toString();

//        holder.text1.setText(name);
        holder.text2.setText(id);


        try {
            docRef.document(currentItem.getFriend_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            User user = task.getResult().toObject(User.class);


                            try {
                                if (!user.getAvatar().equals("") && (user.getAvatar() != null)) {


                                    Glide.with(mContext).load(user.getAvatar()).into(holder.circleImageView2);
                                    AVATAR = user.getAvatar();
                                    holder.text1.setText(user.getUsername());

                                } else {
                                    Glide.with(mContext).load(R.drawable.ic_person).into(holder.circleImageView2);
                                    // AVATAR = user.getAvatar();
                                }
                            } catch (Exception e) {
                                try{
                                    holder.text1.setText(user.getUsername());
                                    Glide.with(mContext).load(R.drawable.ic_person).into(holder.circleImageView2);
                                }catch (Exception s){
                                    Glide.with(mContext).load(R.drawable.ic_person).into(holder.circleImageView2);
                                    holder.text1.setText("Nie Istnieje");
                                    holder.text2.setText("Brak");
                                }


                            }


                        } else {
                            Log.d(VolleyLog.TAG, "No such document");
                        }
                    } else {
                        Log.d(VolleyLog.TAG, "get failed with ", task.getException());
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public Profile_adapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);
        return new Profile_adapter.ExampleViewHolder(v, mListener);
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text1;
        public TextView text2;
        public Button button;
        public CircleImageView circleImageView2;
        private Profile_adapter.OnItemClickListener mListener;

        public ExampleViewHolder(final View itemView, Profile_adapter.OnItemClickListener onItemClickListener) {
            super(itemView);

            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            button = itemView.findViewById(R.id.delete);
            circleImageView2 = itemView.findViewById(R.id.profile_image2);

            mListener = onItemClickListener;
            button.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete: {
                    mListener.onItemClick(getAdapterPosition(), "button");
                    friends.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),friends.size());
                    break;
                }
                default: {
                    mListener.onItemClick(getAdapterPosition(), "itemView");
                }
            }


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String rodzaj);
    }
}
