package com.example.votaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.votaciones.clases.Configuracion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtUsuario, txtClave;
    Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUsuario=findViewById(R.id.txtUsuario);
        txtClave=findViewById(R.id.txtClave);
        btnIngresar=findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String usuario=txtUsuario.getText().toString();
        String clave=txtClave.getText().toString();

        AsyncHttpClient client= new AsyncHttpClient();
        RequestParams parametros= new RequestParams();
        parametros.put("solicitud", "validarUsuario");
        parametros.put("usuario", usuario);
        parametros.put("clave", clave);

        client.post(Configuracion.webService, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Toast.makeText(MainActivity.this, "Recibiendo respuesta del servidor", Toast.LENGTH_LONG).show();
                String respuesta= new String(responseBody);
                ingresar(statusCode, respuesta);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ingresar(statusCode, "Error");

            }
        });
    }

    private void ingresar(int statusCode, String respuesta) {
        if (statusCode==200) {
            try {
                JSONObject datosJSON= new JSONObject(respuesta);
                //Toast.makeText(this, "Respuesta: "+respuesta, Toast.LENGTH_LONG).show();
                if (datosJSON.getBoolean("valido")) {
                    //Toast.makeText(this, "Usuario válido", Toast.LENGTH_LONG).show();
                    finish();
                    Intent intent= new Intent(this, TarjetonActivity.class);
                    intent.putExtra("identificacionVotante", txtUsuario.getText().toString());
                    startActivity(intent);
                    Toast.makeText(this, "El usuario "+txtUsuario.getText().toString()+" ingresó satisfactoriamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error en el formato JSON", Toast.LENGTH_LONG).show();
                throw new RuntimeException(e);
            }
        } else Toast.makeText(MainActivity.this, "Problemas recibir al respuesta del servidor", Toast.LENGTH_LONG).show();
    }
}