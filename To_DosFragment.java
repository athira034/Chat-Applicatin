package com.example.chatapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link To_DosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class To_DosFragment extends Fragment {

    private ArrayList<String> list_of_events=new ArrayList<>();
    private ArrayAdapter<String> itemAdapter;
    private ListView listView;
    private View to_DosFragmentView;
    private List<Messages> userEventList;
    private DatabaseReference EventRef;
    private String userMessageList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public To_DosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment To_dosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static To_DosFragment newInstance(String param1, String param2) {
        To_DosFragment fragment = new To_DosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         to_DosFragmentView  =inflater.inflate(R.layout.fragment_to_dos, container, false);
         EventRef= FirebaseDatabase.getInstance().getReference().child("Events");

       IntializeFields();

       RetrieveAndDisplayEvents();
        setUpListViewListener();
        return to_DosFragmentView;
    }


    private void IntializeFields()
    {
        listView=(ListView) to_DosFragmentView.findViewById(R.id.event_view);
        itemAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_events);
        listView.setAdapter(itemAdapter);

    }

    private void RetrieveAndDisplayEvents()
    {
      EventRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot)
          {
              Set<String> set=new HashSet<>();
              Iterator iterator=snapshot.getChildren().iterator();

              while (iterator.hasNext())
              {
                  set.add(((DataSnapshot)iterator.next()).getKey());
              }


              list_of_events.clear();
              list_of_events.addAll(set);
              itemAdapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    private void setUpListViewListener()
    {


        /*DatabaseReference EventRef =FirebaseDatabase.getInstance().getReference();
        EventRef.child("Events")
                .removeValue();*/

    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
     {
         @Override
         public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
         {

           /*  final int item=i;

             new AlertDialog.Builder(getContext())
                     .setIcon(android.R.drawable.ic_delete)
                     .setTitle("Are you sure?")
                     .setMessage("Do you want to delete this")
                     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i)
                         {

                               deleteEvent();
                             list_of_events.remove(i);
                             itemAdapter.notifyDataSetChanged();
                         }
                     })
                     .setNegativeButton("No",null)
                     .show();
*/

             deleteEvent();
           Context context=getActivity();
             Toast.makeText(context, "Event removed successfully..", Toast.LENGTH_SHORT).show();
             list_of_events.remove(i);
             itemAdapter.notifyDataSetChanged();
             return true;

         }
     });
    }

    private void deleteEvent()
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Events")

                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}