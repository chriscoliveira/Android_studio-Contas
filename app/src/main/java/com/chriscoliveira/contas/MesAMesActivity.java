package com.chriscoliveira.contas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chriscoliveira.contas.banco.Banco;
import com.chriscoliveira.contas.cadastro.CadastroActivity;
import com.chriscoliveira.contas.cartoes.CartaoHomeActivity;
import com.chriscoliveira.contas.despesa.DespesaActivity;
import com.chriscoliveira.contas.login.ConfiguracaoFirebase;
import com.chriscoliveira.contas.renda.RendaActivity;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("deprecation")
public class MesAMesActivity extends AppCompatActivity {
    Toolbar mToolbar;

    private FirebaseAuth auth;
    Banco banco = new Banco();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesames_activity);
        auth= ConfiguracaoFirebase.getFirebaseAutenticacao();
		/*
         * codigo pagina
		 */


        int ano = banco.RetornaAno();

        final TextView tvano = (TextView) findViewById(R.id.tvAnoFiltro);
        tvano.setText(ano + "");
        ImageButton btVoltaMes, btAvancaMes;
        btVoltaMes = (ImageButton) findViewById(R.id.btVoltames);
        btAvancaMes = (ImageButton) findViewById(R.id.btAvancaMes);
        banco.SomaExibeMM(MesAMesActivity.this, tvano.getText().toString());

        btVoltaMes.setOnClickListener(new OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View arg0) {
                tvano.setText("" + (Integer.parseInt(tvano.getText().toString()) - 1));
                banco.SomaExibeMM(MesAMesActivity.this, tvano.getText().toString());
                Log.i("click", "volta");
            }
        });

        btAvancaMes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tvano.setText("" + (Integer.parseInt(tvano.getText().toString()) + 1));
                banco.SomaExibeMM(MesAMesActivity.this, tvano.getText().toString());
            }
        });

		/*
         * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Resumo");


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.action_despesas:
                startActivity(new Intent(this, DespesaActivity.class));
                finish();
                break;
            case R.id.action_rendas:
                startActivity(new Intent(this, RendaActivity.class));
                finish();
                break;
            case R.id.action_cartao:
                startActivity(new Intent(this, CartaoHomeActivity.class));
                finish();
                break;
            case R.id.action_export_email:
                banco.CriaListaParaExporacao(this);
                break;
            case R.id.action_novo:
                startActivity(new Intent(this, CadastroActivity.class));
                finish();
                break;
            case R.id.action_resumo:
                startActivity(new Intent(this, MesAMesActivity.class));
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, AjustesActivity.class));
                finish();
                break;
            case R.id.action_sair:
                deslogarUsuario();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }
    public void deslogarUsuario(){
        try{
            auth.signOut();
            finish();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}
