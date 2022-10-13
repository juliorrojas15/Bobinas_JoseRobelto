package com.app.bobinas_joserobelto.Dialogs;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.app.bobinas_joserobelto.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialogoUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogoUsuario extends DialogFragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refClave = database.getReference("Clave");

    EditText oetClaveActual,oetClaveNueva,oetClaveConfirmacion;
    Button obtnConfirmar;
    Activity Actividad;
    String sClaveActual="";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DialogoUsuario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialogoUsuario.
     */
    // TODO: Rename and change types and number of parameters
    public static DialogoUsuario newInstance(String param1, String param2) {
        DialogoUsuario fragment = new DialogoUsuario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoUsuario();
    }

    private Dialog crearDialogoUsuario() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialogo_usuario,null);
        builder.setView(view);

        oetClaveActual = view.findViewById(R.id.etClaveActual);
        oetClaveNueva = view.findViewById(R.id.etClaveNueva);
        oetClaveConfirmacion = view.findViewById(R.id.etClaveConfirmacion);
        obtnConfirmar = view.findViewById(R.id.btnConfirmar);

        refClave.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sClaveActual = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(Actividad,"No se comunicó a Internet",Toast.LENGTH_SHORT).show();
            }
        });
        eventos();

        return builder.create();
    }

    private void eventos() {

        obtnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sClaveActualIngresada = oetClaveActual.getText().toString();

                if(sClaveActual.equals(sClaveActualIngresada)){
                    if(oetClaveNueva.getText().toString().equals(oetClaveConfirmacion.getText().toString())){
                        refClave.setValue(oetClaveNueva.getText().toString());
                        Toast.makeText(Actividad,"Clave nueva y Clave confirmación diferentes",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    else{
                        Toast.makeText(Actividad,"Clave nueva y Clave confirmación diferentes",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else{
                    Toast.makeText(Actividad,"Clave Actual erronea",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.Actividad = (Activity) context;
        }
        else{
            throw new RuntimeException(context.toString()
                    + " debe implementarse el fragmento listener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

}