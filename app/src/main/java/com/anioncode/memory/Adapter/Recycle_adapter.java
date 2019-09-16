package com.anioncode.memory.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyLog;
import com.anioncode.memory.Models.Places;
import com.anioncode.memory.Models.User;
import com.anioncode.memory.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class Recycle_adapter extends RecyclerView.Adapter<Recycle_adapter.ExampleViewHolder> {


    public ArrayList<Places> pong = new ArrayList();
    private OnItemClickListener mListener;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference docRef = db.collection("USERS");
    public static String AVATAR = "";
    private String button_invisible ;

    public Recycle_adapter(Context mContext, ArrayList<Places> pong, OnItemClickListener mListener ,String button_invisible) {
        this.pong = pong;
        this.mListener = mListener;
        this.mContext = mContext;
        this.button_invisible = button_invisible;
    }

//    public void setOnItemClickListener(OnItemClickListener listener) {
//        mListener = listener;
//    }


    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);
        return new ExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        Places currentItem = pong.get(position);


        String creatorName = currentItem.getName().toString();
        String likeCount = currentItem.getTimestamp().toString();

        holder.text1.setText(creatorName);
        holder.text2.setText(likeCount);
        if (button_invisible=="NIE"){
            holder.button.setVisibility(View.INVISIBLE);
        }
//        Glide
//                .with(mContext)
//                .load("https://cdn.gallerystore.pl/works//w350-h1750/ewa-narloch-kerszka-drzewo-zycia-grafika.jpg")
//                .into(holder.circleImageView);

        docRef.document(currentItem.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        User user = task.getResult().toObject(User.class);

                        try {
                            Glide.with(mContext).load(user.getAvatar()).into(holder.circleImageView2);
                            AVATAR = user.getAvatar();
                        } catch (Exception e) {
                            Glide.with(mContext).load(R.drawable.ic_person).into(holder.circleImageView2);


                        }


                    } else {
                        Log.d(VolleyLog.TAG, "No such document");
                    }
                } else {
                    Log.d(VolleyLog.TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pong.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text1;
        public TextView text2;
        public Button button;
        public CircleImageView circleImageView2;
        private OnItemClickListener mListener;

        public ExampleViewHolder(final View itemView, OnItemClickListener onItemClickListener) {
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
                    pong.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),pong.size());
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
