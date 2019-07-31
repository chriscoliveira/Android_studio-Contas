package com.chriscoliveira.contas;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chriscoliveira.contas.banco.Banco;
import com.chriscoliveira.contas.cadastro.CadastroActivity;
import com.chriscoliveira.contas.despesa.DespesaActivity;
import com.chriscoliveira.contas.helper.Permissao;
import com.chriscoliveira.contas.login.UsuarioFirebase;
import com.chriscoliveira.contas.renda.RendaActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Date;

@SuppressWarnings("deprecation")
public class AjustesActivity extends AppCompatActivity {
    Toolbar mToolbar;
    FirebaseStorage storage;
    private StorageReference storageReference;
    //int pos = -1;
    //int consulta = 0;
    //Button btFiltrar;
    private String[] permissoesNecessarias = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE

    };
    Banco banco = new Banco();
    //ZUtilitarios zutil = new ZUtilitarios();

    //Button btGravar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_activity);
         /*
         * exibe data de compilação
		 */
        TextView tvVersao = (TextView) findViewById(R.id.tvVersao);
        Date buildDate = new Date(BuildConfig.BUILD_TIME);
        tvVersao.setText(" " + buildDate.toString());

        Permissao.validarPermissoes(this, permissoesNecessarias, 1);



		/*
         * codigo pagina
		 */

        Button btImportar = (Button) findViewById(R.id.btImportar);
        Button btExportar = (Button) findViewById(R.id.btExportar);
        Button btLimpar = (Button) findViewById(R.id.btLimpar);

        btImportar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //importa da nuvem

                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                String userId= UsuarioFirebase.getIdUser();

                final String filename4 = "bancoContas.txt";

                StorageReference islandRef4 = storageReference.child(userId).child("bancodados").child(filename4);
                File rootPath4 = new File(Environment.getExternalStorageDirectory(), "Download");
                if (!rootPath4.exists()) {
                    rootPath4.mkdirs();
                }

                final File localFile4 = new File(rootPath4, filename4);
                islandRef4.getFile(localFile4).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AjustesActivity.this, "Arquivo baixado " + localFile4.toString(), Toast.LENGTH_SHORT).show();
                        //importa para o SQLite



                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(AjustesActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(AjustesActivity.this);
                        }
                        builder.setTitle("Importar registros")
                                .setMessage("\nTem certeza que deseja importar Registros para o banco?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        banco.importarLista(AjustesActivity.this);
                                        startActivity(new Intent(AjustesActivity.this, MainActivity.class));
                                        AjustesActivity.this.finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        startActivity(new Intent(AjustesActivity.this, MainActivity.class));
                                        AjustesActivity.this.finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        //fim da importacao do SQLite

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AjustesActivity.this, "Erro ao baixar o arquivo : " + localFile4.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

                //fim da importacao nuvem
            }
        });
        btExportar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AjustesActivity.this, Exportar.class));
                finish();
                //upload();
            }
        });
        btLimpar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder CaixaAlerta = new AlertDialog.Builder(AjustesActivity.this);
                CaixaAlerta.setMessage("Confirma a limpeza do banco?");
                CaixaAlerta.setTitle("APAGAR TUDO?");
                CaixaAlerta.setPositiveButton("SIM",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                banco.deletar(AjustesActivity.this);
                            }
                        });
                CaixaAlerta.setNegativeButton("NAO", null);
                CaixaAlerta.show();

            }
        });

		/*
         * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Ajustes");


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //FirebaseUser usuarioRecebe = UsuarioFirebase.getUsuarioAtual();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

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

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }




}
