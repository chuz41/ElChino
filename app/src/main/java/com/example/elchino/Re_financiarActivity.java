package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Re_financiarActivity extends AppCompatActivity {

    boolean flag_client_reciv = false;
    String cliente_recibido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_financiar);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (mensaje_recibido.equals("")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        if (cliente_recibido.equals("")) {
            //Do nothing.
        } else {
            flag_client_reciv = true;
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