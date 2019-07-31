package com.chriscoliveira.contas.cartoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.chriscoliveira.contas.AjustesActivity;
import com.chriscoliveira.contas.MainActivity;
import com.chriscoliveira.contas.MesAMesActivity;
import com.chriscoliveira.contas.R;
import com.chriscoliveira.contas.banco.Banco;
import com.chriscoliveira.contas.banco.ZUtilitarios;
import com.chriscoliveira.contas.cadastro.CadastroActivity;
import com.chriscoliveira.contas.despesa.DespesaActivity;
import com.chriscoliveira.contas.login.ConfiguracaoFirebase;
import com.chriscoliveira.contas.renda.RendaActivity;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("deprecation")
public class CartaoHomeActivity extends AppCompatActivity {
    Toolbar mToolbar;
    int pos = -1;
    private FirebaseAuth auth;
    TextView tvmes, tvano;
    Banco banco = new Banco();
    ZUtilitarios zutil = new ZUtilitarios();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cartao_home_activity);

        auth= ConfiguracaoFirebase.getFirebaseAutenticacao();
		/*
         * codigo pagina
		 */
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        banco.ExibeBotaoCartoes(this);


		/*
         * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Cartao");


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void CartaoItau(View view){
        startActivity(new Intent(this, CartaoItauActivity.class));
        finish();
    }
    public void CartaoBradesco(View view){
        startActivity(new Intent(this, CartaoBradescoActivity.class));
        finish();
    }
    public void CartaoCaixa(View view){
        startActivity(new Intent(this, CartaoCaixaActivity.class));
        finish();
    }
    public void CartaoNubank(View view){
        startActivity(new Intent(this, CartaoNubankActivity.class));
        finish();
    }
    public void CartaoMarisa(View view){
        startActivity(new Intent(this, CartaoMarisaActivity.class));
        finish();
    }
    public void CartaoCea(View view){
        startActivity(new Intent(this, CartaoCeaActivity.class));
        finish();
    }
    public void CartaoHipercard(View view){
        startActivity(new Intent(this, CartaoHipercardActivity.class));
        finish();
    }
    public void CartaoRiachuelo(View view){
        startActivity(new Intent(this, CartaoRiachueloActivity.class));
        finish();
    }
    public void CartaoPernambucanas(View view){
        startActivity(new Intent(this, CartaoPernambucanasActivity.class));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.action_rendas:
                startActivity(new Intent(this, RendaActivity.class));
                finish();
                break;
            case R.id.action_despesas:
                startActivity(new Intent(this, DespesaActivity.class));
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
