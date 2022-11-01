package com.app.bobinas_joserobelto;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bobinas_joserobelto.Dialogs.DialogoConfirmacion;
import com.app.bobinas_joserobelto.Dialogs.DialogoEstadisticas;
import com.app.bobinas_joserobelto.Dialogs.DialogoUsuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    //##############################################################################################    BASES DE DATOS
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refBobina1_OnOff = database.getReference("Bobinas/Bobina 1/On_Off");
    DatabaseReference refBobina2_OnOff = database.getReference("Bobinas/Bobina 2/On_Off");
    DatabaseReference refBobina3_OnOff = database.getReference("Bobinas/Bobina 3/On_Off");
    DatabaseReference refBobina4_OnOff = database.getReference("Bobinas/Bobina 4/On_Off");
    DatabaseReference refBobina5_OnOff = database.getReference("Bobinas/Bobina 5/On_Off");
    DatabaseReference refBobina6_OnOff = database.getReference("Bobinas/Bobina 6/On_Off");
    DatabaseReference refBobina7_OnOff = database.getReference("Bobinas/Bobina 7/On_Off");
    DatabaseReference refBobina8_OnOff = database.getReference("Bobinas/Bobina 8/On_Off");
    
    DatabaseReference refTiempoRestante = database.getReference("/TiempoRestante");
    DatabaseReference refEstadoGeneral = database.getReference("/EstadoGeneral");

    //##############################################################################################    FRONT END

    Button obtnEnviarOrden, obtnUsuario;
    RadioButton orbOpcion_0,orbOpcion_1,orbOpcion_2,orbOpcion_3,orbOpcion_4,orbOpcion_5,orbOpcion_6,
                orbOpcion_7,orbOpcion_8;
    TextView otvTiempoActual;

    int iEstadoGeneral =0;
    boolean[] abBobinasOnOff = new boolean[8];
    boolean value;

    //##############################################################################################    ON CREATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //#########################################################################################     Realaciónes FrontEnd

        obtnUsuario = (Button) findViewById(R.id.btnUsuario);
        obtnEnviarOrden = (Button) findViewById(R.id.btnEnviarOrden);
        otvTiempoActual = (TextView) findViewById(R.id.tvTiempoActual);
        orbOpcion_0 = (RadioButton) findViewById(R.id.rbOpcion_0);
        orbOpcion_1 = (RadioButton) findViewById(R.id.rbOpcion_1);
        orbOpcion_2 = (RadioButton) findViewById(R.id.rbOpcion_2);
        orbOpcion_3 = (RadioButton) findViewById(R.id.rbOpcion_3);
        orbOpcion_4 = (RadioButton) findViewById(R.id.rbOpcion_4);
        orbOpcion_5 = (RadioButton) findViewById(R.id.rbOpcion_5);
        orbOpcion_6 = (RadioButton) findViewById(R.id.rbOpcion_6);
        orbOpcion_7 = (RadioButton) findViewById(R.id.rbOpcion_7);
        orbOpcion_8 = (RadioButton) findViewById(R.id.rbOpcion_8);

        //###################################################################################       Lecturas iniciales

        refTiempoRestante.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    int iTiempoRestante = Integer.parseInt(task.getResult().getValue().toString());
                    int iMinutosRestantes = (iTiempoRestante/60);
                    int iSegundosRestantes = iTiempoRestante - (iMinutosRestantes * 60);
                    String myTime;
                    if (iSegundosRestantes<10){
                        myTime =  iMinutosRestantes + ":0" + iSegundosRestantes;
                    }
                    else{
                        myTime =  iMinutosRestantes + ":" + iSegundosRestantes;
                    }
                    otvTiempoActual.setText("Quedan " + myTime + " minutos");
                }
            }
        });

        refEstadoGeneral.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    iEstadoGeneral = Integer.parseInt(task.getResult().getValue().toString());
                    fnFondoOpciones(iEstadoGeneral,true);
                    switch (iEstadoGeneral){
                        case 0: orbOpcion_0.toggle();break;
                        case 1: orbOpcion_1.toggle();break;
                        case 2: orbOpcion_2.toggle();break;
                        case 3: orbOpcion_3.toggle();break;
                        case 4: orbOpcion_4.toggle();break;
                        case 5: orbOpcion_5.toggle();break;
                        case 6: orbOpcion_6.toggle();break;
                        case 7: orbOpcion_7.toggle();break;
                        case 8: orbOpcion_8.toggle();break;
                    }
                }
            }
        });



        //###################################################################################       ACCIONES DE BOTONES

        obtnEnviarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(orbOpcion_0.isChecked()) iEstadoGeneral = 0;
                else if(orbOpcion_1.isChecked()) iEstadoGeneral = 1;
                else if(orbOpcion_2.isChecked()) iEstadoGeneral = 2;
                else if(orbOpcion_3.isChecked()) iEstadoGeneral = 3;
                else if(orbOpcion_4.isChecked()) iEstadoGeneral = 4;
                else if(orbOpcion_5.isChecked()) iEstadoGeneral = 5;
                else if(orbOpcion_6.isChecked()) iEstadoGeneral = 6;
                else if(orbOpcion_7.isChecked()) iEstadoGeneral = 7;
                else if(orbOpcion_8.isChecked()) iEstadoGeneral = 8;
                else{
                    Toast.makeText(MainActivity.this,"No ha seleccionado bobina",Toast.LENGTH_SHORT).show();
                    return;
                }
                fnFondoOpciones(0,true);

                Bundle args = new Bundle();
                args.putString("iEstadoGeneral",String.valueOf(iEstadoGeneral));
                DialogoConfirmacion dialogoConfirmacion = new DialogoConfirmacion();
                dialogoConfirmacion.setArguments(args);
                dialogoConfirmacion.show(getSupportFragmentManager(),"Dialogo Confirmar");
            }
        });

        obtnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogoUsuario dialogoUsuario = new DialogoUsuario();
                dialogoUsuario.show(getSupportFragmentManager(),"Dialogo Usuario");
            }
        });



        //########################################################################################## Lectura de Datos de accionamiento
        refBobina1_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(1,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina2_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(2,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina3_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(3,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina4_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(4,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina5_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(5,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina6_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(6,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina7_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(7,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refBobina8_OnOff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fnFondoOpciones(8,dataSnapshot.getValue(boolean.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //########################################################################################## Lectura de Datos de accionamiento
        refTiempoRestante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(iEstadoGeneral>0){
                    int iTiempoRestante = dataSnapshot.getValue(int.class);
                    int iMinutosRestantes = (iTiempoRestante/60);
                    int iSegundosRestantes = iTiempoRestante - (iMinutosRestantes * 60);
                    String myTime;
                    if (iSegundosRestantes<10){
                        myTime =  iMinutosRestantes + ":0" + iSegundosRestantes;
                    }
                    else{
                        myTime =  iMinutosRestantes + ":" + iSegundosRestantes;
                    }
                    otvTiempoActual.setText("Quedan " + myTime + " minutos");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }

    void fnFondoOpciones(int iBobina, boolean bEstado){

        if (iBobina == 1) {
            if (bEstado == true) orbOpcion_1.setBackgroundColor(Color.GREEN);
            else orbOpcion_1.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 2) {
            if (bEstado == true) orbOpcion_2.setBackgroundColor(Color.GREEN);
            else orbOpcion_2.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 3) {
            if (bEstado == true) orbOpcion_3.setBackgroundColor(Color.GREEN);
            else orbOpcion_3.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 4) {
            if (bEstado == true) orbOpcion_4.setBackgroundColor(Color.GREEN);
            else orbOpcion_4.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 5) {
            if (bEstado == true) orbOpcion_5.setBackgroundColor(Color.GREEN);
            else orbOpcion_5.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 6) {
            if (bEstado == true) orbOpcion_6.setBackgroundColor(Color.GREEN);
            else orbOpcion_6.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 7) {
            if (bEstado == true) orbOpcion_7.setBackgroundColor(Color.GREEN);
            else orbOpcion_7.setBackgroundColor(Color.WHITE);
        }
        if (iBobina == 8) {
            if (bEstado == true) orbOpcion_8.setBackgroundColor(Color.GREEN);
            else orbOpcion_8.setBackgroundColor(Color.WHITE);
        }
        if(iEstadoGeneral == 0) orbOpcion_0.setBackgroundColor(Color.GREEN);
        else orbOpcion_0.setBackgroundColor(Color.WHITE);

    }

}