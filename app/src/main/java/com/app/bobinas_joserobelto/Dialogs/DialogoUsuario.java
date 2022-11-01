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

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialogoUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogoUsuario extends DialogFragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsuarios = database.getReference("Usuarios");

    EditText oetRegistrarCedula, oetRegistrarClave,oetRegistrarConfirmar;
    EditText oetCambiarCedula,oetCambiarClaveActual, oetCambiarClaveNueva,oetCambiarClaveConfirmar;

    Button obtnRegistrar,obtnConfirmar;
    Activity Actividad;

    Map<String, Object> mapUsuarios = new HashMap<>();

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

        oetRegistrarCedula = view.findViewById(R.id.etRegirtrarCedula);
        oetRegistrarClave = view.findViewById(R.id.etRegitrarClave);
        oetRegistrarConfirmar = view.findViewById(R.id.etRegistrarConfirmar);

        oetCambiarCedula = view.findViewById(R.id.etCambiarCedula);
        oetCambiarClaveActual = view.findViewById(R.id.etCambiarClaveActual);
        oetCambiarClaveNueva = view.findViewById(R.id.etCambiarClaveNueva);
        oetCambiarClaveConfirmar = view.findViewById(R.id.etCambiarClaveConfirmar);

        obtnRegistrar = view.findViewById(R.id.btnRegistrar);
        obtnConfirmar = view.findViewById(R.id.btnConfirmar);


        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i = 0;
                    for(DataSnapshot d : snapshot.getChildren()) {
                       mapUsuarios.put(d.getKey(),d.getValue().toString());
                       i++;
                    }
                    Toast.makeText(Actividad,"DAtos Obtenidos",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        eventos();

        return builder.create();
    }

    private void eventos() {

         Map<String, Object> mapNuevoUsuario = new HashMap<>();
        obtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sCedula = oetRegistrarCedula.getText().toString();
                String sClaveNueva = oetRegistrarClave.getText().toString();
                String sClaveConfirmar = oetRegistrarConfirmar.getText().toString();

                if (sCedula.isEmpty() || sClaveNueva.isEmpty() || sClaveConfirmar.isEmpty()){
                    Toast.makeText(Actividad,"Faltan datos por ingresar",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mapUsuarios.containsKey(sCedula)){
                    Toast.makeText(Actividad,"Este Usuario ya existe",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(sClaveNueva.equals(sClaveConfirmar)){
                    mapNuevoUsuario.put(sCedula,sClaveNueva);
                    refUsuarios.updateChildren(mapNuevoUsuario);
                    Toast.makeText(Actividad,"Usuario Registrado Satisfactoriamente",Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else{
                    Toast.makeText(Actividad,"Clave nueva y Clave de confirmación diferentes",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        obtnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sCedula = oetCambiarCedula.getText().toString();
                String sClaveActual = oetCambiarClaveActual.getText().toString();
                String sClaveNueva = oetCambiarClaveNueva.getText().toString();
                String sClaveConfirmar = oetCambiarClaveConfirmar.getText().toString();

                if (sCedula.isEmpty() || sClaveActual.isEmpty() || sClaveNueva.isEmpty() || sClaveConfirmar.isEmpty()){
                    Toast.makeText(Actividad,"Faltan datos por ingresar",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mapUsuarios.containsKey(sCedula)){
                    Toast.makeText(Actividad,"Este Usuario NO EXISTE",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mapUsuarios.get(sCedula).equals(sClaveActual)){
                    Toast.makeText(Actividad,"La clave de usuario NO CORRESPONDE",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!sClaveNueva.equals(sClaveConfirmar)){
                    Toast.makeText(Actividad,"Clave nueva y Clave de confirmación diferentes",Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference refNuevaClave = database.getReference("Usuarios/" + sCedula);
                refNuevaClave.setValue(sClaveNueva);
                Toast.makeText(Actividad,"Clave de Usuario actualizada satisfactoriamente",Toast.LENGTH_SHORT).show();
                dismiss();
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