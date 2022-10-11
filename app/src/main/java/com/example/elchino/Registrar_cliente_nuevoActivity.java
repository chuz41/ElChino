package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

public class Registrar_cliente_nuevoActivity extends AppCompatActivity {

    TextInputLayout nombre_cliente,apellido1_cliente,apellido2_cliente,apodo_cliente,edad_cliente,sexo_cliente,direccion_cliente,puntuacion_cliente,tasa_cliente,monto_cliente,monto_disponible,interes_mora,ID_cliente;
    private String u1,u2,u3,u4,u5,u6,u7,u8,u9,u10,u11,u12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cliente_nuevo);
    }

    public boolean valid_nombre_cliente () {
        if (nombre_cliente.getEditText()!=null){
            u1 = nombre_cliente.getEditText().getText().toString().trim();
        }

        if (u1.isEmpty()) {

            nombre_cliente.setError(getText(R.string.cantempty_nombre));
            return false;

        }

        else if (u1.length() > 12) {

            nombre_cliente.setError(getText(R.string.toolong));
            return false;
        }

        else {
            nombre_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            return true;
        }

    }

    public boolean valid_apellido1 () {
        if (apellido1_cliente.getEditText()!=null){
            u2 = apellido1_cliente.getEditText().getText().toString().trim();
        }

        if (u2.isEmpty()) {

            apellido1_cliente.setError(getText(R.string.cantempty_apellido1));
            return false;

        }

        else if (u2.length() > 15) {

            apellido1_cliente.setError(getText(R.string.toolong_apellido1));
            return false;
        }

        else {
            apellido1_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            return true;
        }

    }

    public boolean valid_apellido2 () {
        if (apellido2_cliente.getEditText()!=null){
            u3 = apellido2_cliente.getEditText().getText().toString().trim();
        }

        if (u3.isEmpty()) {

            apellido2_cliente.setError(getText(R.string.cantempty_apellido1));
            return false;

        }

        else if (u3.length() > 15) {

            apellido2_cliente.setError(getText(R.string.toolong_apellido1));
            return false;
        }

        else {
            apellido2_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            return true;
        }

    }

    public boolean valid_apodo () {
        if (apodo_cliente.getEditText()!=null){
            u4 = apodo_cliente.getEditText().getText().toString().trim();
        }

        if (u4.isEmpty()) {

            apodo_cliente.setError(getText(R.string.cantempty_apodo));
            return false;

        }

        else if (u4.length() > 15) {

            apodo_cliente.setError(getText(R.string.toolong_apodo));
            return false;
        }

        else {
            apodo_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            return true;
        }

    }

    @Override
    public void onBackPressed(){
        boton_atras();
    }

    private void boton_atras() {
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }
}