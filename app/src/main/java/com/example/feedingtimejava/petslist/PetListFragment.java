package com.example.feedingtimejava.petslist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedingtimejava.MainActivity;
import com.example.feedingtimejava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PetListFragment extends Fragment {

    private MainActivity mainActivity;
    private RecyclerView petRecyclerView;
    private PetAdapter petAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_petlist, container, false);

        petRecyclerView = (RecyclerView) view.findViewById(R.id.rvPets);
        petRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //generatePets();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // This is where we retrieve from Firebase
        ArrayList<PetItem> petItems = new ArrayList<PetItem>();
        db.collection("Lyman")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.w("FirePET", document.getId() + " => " + document.getData());
                                Log.w("FirePet", document.getData().get("last_fed").toString());
                                petItems.add(new PetItem(document.getId(), (Timestamp) document.getData().get("last_fed")));
                            }
                            petAdapter = new PetAdapter(petItems);
                            petRecyclerView.setAdapter(petAdapter);

                        } else {
                            Log.w("FirePET", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

//    private void generatePets(){
//        ArrayList<PetItem> petItems = new ArrayList<PetItem>();
//        for (Integer i = 0; i < 25; i++) {
//            petItems.add(new PetItem("Pet number: " + i.toString()));
//        }
//
//        petAdapter = new PetAdapter(petItems);
//        petRecyclerView.setAdapter(petAdapter);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    private class PetHolder extends RecyclerView.ViewHolder {

        public TextView petNameView;
        public TextView petLastFed;
        public PetItem petItem;
        public Switch aSwitch;

        public PetHolder(View itemView){
            super(itemView);

            petNameView = (TextView) itemView.findViewById(R.id.pet_item_text);
            petLastFed = (TextView) itemView.findViewById(R.id.pet_fed_text);
            aSwitch = (Switch) itemView.findViewById(R.id.pet_item_switch);

            itemView.setOnClickListener(v -> {
                Toast.makeText(getContext(), "You pressed on: " + petItem.textValue, Toast.LENGTH_SHORT).show();
            });


            // Add an event for when the switch is pressed
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getContext(), petItem.textValue + " has been fed", Toast.LENGTH_SHORT).show();

                        db.collection("Lyman").document(petItem.textValue)
                                .update("last_fed", Timestamp.now())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                    else {
                        Toast.makeText(getContext(), petItem.textValue + " has not been fed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private class PetAdapter extends RecyclerView.Adapter<PetHolder>{

        private List<PetItem> mValues;

        public PetAdapter(List<PetItem> values){
            mValues = values;
        }

        @Override
        public PetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.pet_list_item, parent, false);

            return new PetHolder(view);
        }

        @Override
        public void onBindViewHolder(PetHolder holder, int position) {
            holder.petItem = mValues.get(position);
            holder.petNameView.setText(holder.petItem.textValue);
            holder.petLastFed.setText("LAST FED: " + holder.petItem.last_fed.toDate().toString());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}


