package com.app.bobinas_joserobelto.Dialogs;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.widget.RelativeLayout.TRUE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bobinas_joserobelto.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialogoConfirmacion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogoConfirmacion extends DialogFragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refEstadoGeneral = database.getReference("EstadoGeneral");
    DatabaseReference refTiempoGeneral = database.getReference("TiempoGeneral");
    DatabaseReference refUsuarios = database.getReference("Usuarios");

    EditText etCedula,etClaveUsuario;
    Button btnConfirmar,btnVolver;
    TextView tvFraseMinutos,tvClaveRespuesta;
    NumberPicker npkTiempo;
    Activity Actividad;
    int iEstadoGeneral;
    String sClaveActual="";
    Map<String, Object> mapUsuarios = new HashMap<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DialogoConfirmacion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialogoConfirmacion.
     */
    // TODO: Rename and change types and number of parameters
    public static DialogoConfirmacion newInstance(String param1, String param2) {
        DialogoConfirmacion fragment = new DialogoConfirmacion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoConfirmación();
    }

    private Dialog crearDialogoConfirmación() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialogo_confirmacion,null);
        builder.setView(view);

        iEstadoGeneral = Integer.valueOf(getArguments().getString("iEstadoGeneral"));

        etCedula = view.findViewById(R.id.etCedula);
        etClaveUsuario = view.findViewById(R.id.etClaveUsuario);
        tvClaveRespuesta = view.findViewById(R.id.tvClaveRespuesta);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);
        btnVolver = view.findViewById(R.id.btnVolver);
        npkTiempo = view.findViewById(R.id.npkTiempo);
        tvFraseMinutos = view.findViewById(R.id.tvFraseMinutos);

        etCedula.requestFocus();

        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i = 0;
                    for(DataSnapshot d : snapshot.getChildren()) {
                        mapUsuarios.put(d.getKey(),d.getValue().toString());
                        i++;
                    }
                    Toast.makeText(Actividad,"Datos Obtenidos",Toast.LENGTH_SHORT).show();
                    btnConfirmar.setVisibility(View.INVISIBLE);
                    tvClaveRespuesta.setText("Clave INCORRECTA");
                    tvClaveRespuesta.setTextColor(RED);
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

        if(iEstadoGeneral == 0){
            npkTiempo.setVisibility(View.INVISIBLE);
            tvFraseMinutos.setVisibility(View.INVISIBLE);
        }
        else {
            npkTiempo.setVisibility(View.VISIBLE);
            tvFraseMinutos.setVisibility(View.VISIBLE);
            if (npkTiempo != null) {
                npkTiempo.setMinValue(1);
                npkTiempo.setMaxValue(60);
                npkTiempo.setWrapSelectorWheel(true);
                npkTiempo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    }
                });
            }
        }
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Actividad,npkTiempo.getValue()
                                + " minutos en la bobina "
                                + iEstadoGeneral,Toast.LENGTH_SHORT).show();
                refEstadoGeneral.setValue(iEstadoGeneral);
                refTiempoGeneral.setValue(npkTiempo.getValue());
                dismiss();
            }
        });
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        etCedula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(mapUsuarios.containsKey(editable.toString()) &&
                        mapUsuarios.get(editable.toString()).equals(etClaveUsuario.getText().toString())){
                    btnConfirmar.setVisibility(View.VISIBLE);
                    tvClaveRespuesta.setText("Usuario CORRECTO");
                    tvClaveRespuesta.setTextColor(GREEN);
                }
                else{
                    btnConfirmar.setVisibility(View.INVISIBLE);
                    tvClaveRespuesta.setText("Clave y/o usuario INCORRECTOS");
                    tvClaveRespuesta.setTextColor(RED);
                }
            }
        });


        etClaveUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().equals(mapUsuarios.get(etCedula.getText().toString()))){
                    btnConfirmar.setVisibility(View.VISIBLE);
                    tvClaveRespuesta.setText("Usuario CORRECTO");
                    tvClaveRespuesta.setTextColor(GREEN);
                }
                else{
                    btnConfirmar.setVisibility(View.INVISIBLE);
                    tvClaveRespuesta.setText("Clave y/o usuario INCORRECTOS");
                    tvClaveRespuesta.setTextColor(RED);
                }
            }
        });

        etClaveUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    hideKeyboard(view);
                }
            }

        });


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)Actividad.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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