package com.example.attendanceapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.attendanceapp.adapter.AttendanceAdapter;
import com.example.attendanceapp.databinding.FragmentTeacherMainBinding;
import com.example.attendanceapp.model.Attendance;
import com.example.attendanceapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherMainFragment extends Fragment implements AdapterView.OnItemSelectedListener, RecyclerViewInterface {

    FragmentTeacherMainBinding binding;

    FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    LinearLayoutManager layoutManager;

    private AttendanceAdapter attendanceAdapter;
    private ArrayList<User> attendancearraylist;
    ArrayList<String> dateslist = new ArrayList<String>();

    Date d = new Date();
    SimpleDateFormat enDate = new SimpleDateFormat("MMMM d, yyyy ", new Locale("en"));
    String en = enDate.format(d);
    CharSequence s = DateFormat.format("MMMM d, yyyy ", d.getTime());
    File filePath = new File(Environment.getExternalStorageDirectory() + "/Attendance.xls/");


    // String[] arrayList = {"sub1", "sub2", "sub3"};

    BottomSheetDialog dialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TeacherMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherMainFragment newInstance(String param1, String param2) {
        TeacherMainFragment fragment = new TeacherMainFragment();
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
        binding = FragmentTeacherMainBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        binding.coursesspinner.setOnItemSelectedListener(this);


        firestore.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("subs");

                    Log.d("ssTAG", "onComplete: " + group);

                    ArrayAdapter ad = new ArrayAdapter(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            group);

                    binding.coursesspinner.setAdapter(ad);

                }
            }
        });

        binding.excelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateExcel();

            }
        });


        binding.scantea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        dialog = new BottomSheetDialog(getActivity());

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.coursesspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initRecycler();
                RetrieveNewsData();
                getAllFieldsdates();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return binding.getRoot();
    }

    private void CreateExcel() {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(binding.coursesspinner.getSelectedItem().toString());

        for (int i = 0; i < dateslist.size(); i++) {

            HSSFRow hssfRow = hssfSheet.createRow(i);
            HSSFCell hssfCell = hssfRow.createCell(i);


            hssfCell.setCellValue(dateslist.get(i));
            Log.d("CreateExcel:", "CreateExcel: " + dateslist.get(i));

        }


        try {
            if (!filePath.exists()) {

                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText(getActivity(), "File created , you can find it at your  storage ", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private static HashMap<String, Object>
    convertArrayListToHashMap(ArrayList<String> arrayList) {

        HashMap<String, Object> hashMap = new HashMap<>();

        for (String str : arrayList) {

            hashMap.put(str, str.length());
        }

        return hashMap;
    }

    private void getAllFieldsdates() {


        final ObjectMapper mapper = new ObjectMapper();
        final Attendance pojo;


        firestore
                .collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(binding.coursesspinner.getSelectedItem().toString())
                .document(binding.coursesspinner.getSelectedItem().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> map = task.getResult().getData();

//todo : get date list here and import it to excel
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    dateslist.add(entry.getValue().toString());
                                    Log.d("dateslist", entry.getKey());// string key
                                    Log.d("dateslist2", "\n" + dateslist);//object


                                }
                            }


                        }
                    }
                });

    }


    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlauncher.launch(options);

    }

    private void initRecycler() {
        attendancearraylist = new ArrayList<User>();
        layoutManager = new LinearLayoutManager(getActivity());
        binding.attendancerecycler.setLayoutManager(layoutManager);
        binding.attendancerecycler.setHasFixedSize(true);

        String course = binding.coursesspinner.getSelectedItem().toString();


        attendanceAdapter = new AttendanceAdapter(this, attendancearraylist, firestore, getContext(), course);
        binding.attendancerecycler.setAdapter(attendanceAdapter);
    }

    private void RetrieveNewsData() {


        if (binding.coursesspinner != null && binding.coursesspinner.getSelectedItem() != null) {
            firestore.collection("User")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection(binding.coursesspinner.getSelectedItem().toString())
                    .document(binding.coursesspinner.getSelectedItem().toString())
                    .collection("Students")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {

                                Log.d("fireStore Error", error.getMessage().toString());

                                return;
                            }
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                    attendancearraylist.add(documentChange.getDocument().
                                            toObject(User.class));


                                }

                                attendanceAdapter.notifyDataSetChanged();


                            }


                        }
                    });
        } else {
//            firestore.collection("User").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()){
//                        List<String> group = (List<String>) task.getResult().get("subs");
//
//                        firestore.collection("User")
//                                .document(id)
//                                .collection(binding.courseadminspinner.getSelectedItem().toString())
//                                .document(binding.courseadminspinner.getSelectedItem().toString())
//                                .collection("Students")
//                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                        if (error != null) {
//
//                                            Log.d("fireStore Error", error.getMessage().toString());
//
//                                            return;
//                                        }
//                                        for (DocumentChange documentChange : value.getDocumentChanges()) {
//                                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
//
//                                                studentArrayList.add(documentChange.getDocument().
//                                                        toObject(User.class));
//
//
//
//                                            }
//
//                                            newStudentAdapterAdapter.notifyDataSetChanged();
//
//
//                                        }
//
//
//                                    }
//                                });
//
//                    }
//                }
//            });
        }


    }


    ActivityResultLauncher<ScanOptions> barlauncher = registerForActivityResult(new ScanContract(), result ->
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (result.getContents() != null) {

            builder.setTitle(result.getContents());
            Attendance attendance2 = new Attendance(result.getContents(), en);
            firestore.collection("User").document(mAuth.getCurrentUser().getUid()).collection(binding.coursesspinner.getSelectedItem().toString()).document(binding.coursesspinner.getSelectedItem().toString()).update(en, FieldValue.arrayUnion(attendance2)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "student Added", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            builder.setTitle("No user been Added");
        }

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    });


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        firestore.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("subs");

                    Toast.makeText(getActivity(), "" + group.get(i), Toast.LENGTH_SHORT).show();


                }
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(Integer position) {

    }

    @Override
    public void onDeleteClick(Integer position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }
}