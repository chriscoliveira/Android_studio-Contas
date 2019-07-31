package com.chriscoliveira.contas;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chriscoliveira.contas.banco.Banco;
import com.chriscoliveira.contas.helper.Permissao;
import com.chriscoliveira.contas.login.ConfiguracaoFirebase;
import com.chriscoliveira.contas.login.UsuarioFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Exportar extends AppCompatActivity {

    Banco banco = new Banco();

    protected SQLiteDatabase bancoDados = null;
    public static String NOME_BANCO = "MinhasContas";
    public static String NOME_TABELA_CONTAS = "Contas";
    public static String NOME_TABELA_CATEGORIAS = "Categorias";
    public static String ID = "_id";
    public static String CONTA = "conta";
    public static String VALOR = "valor";
    public static String ANO = "ano";
    public static String MES = "mes";
    public static String DIA = "dia";
    public static String PARCELA = "parcela";
    public static String SITUACAO = "situacao";
    public static String TIPO = "tipo";
    public static String CATEGORIA = "categoria";

    Cursor cursor;
    String Valores = "";
    Dialog dialog;
    CheckBox cbSituacao;
    Button btAcaoDialog, btAcaoApagar;
    String txtcb;
    Toolbar mToolbar;


    FirebaseStorage storage;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private String[] permissoesNecessarias = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_exportar);

        Permissao.validarPermissoes(this, permissoesNecessarias, 1);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

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


    public void enviarEmail() {

            // prepara o conteudo do email

            Date date = new Date();
            String stringDate = DateFormat.getDateTimeInstance().format(date);
            String assunto = "Minhas Contas - backup";

            String messagem = "Segue em anexo um backup dos dados - " + stringDate ;


            //anexa os arquivos ao email
            String filename1 = "bancoContas.txt";
            File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "Download", filename1);
            Uri contentUri1 = FileProvider.getUriForFile(this, "com.chriscoliveira.contas", file1);


            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(contentUri1);



            //declara os itens para o compartilhamento
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uris);
            shareIntent.setType("text/plain");
            FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
            String email = usuario.getCurrentUser().getEmail();

            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, assunto);
            shareIntent.putExtra(Intent.EXTRA_TEXT, messagem);

            startActivity(Intent.createChooser(shareIntent, ""));

    }


    public void upload() {
        try {
            SimpleDateFormat formataData = new SimpleDateFormat("ddMMyy_HHmm");
            Date data = new Date();
            String dataFormatada = formataData.format(data);

            String userId= UsuarioFirebase.getIdUser();

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            File sdcard = Environment.getExternalStorageDirectory();
            final String filename1 = "bancoContas.txt";


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //TODO file1

            Uri file1 = Uri.fromFile(new File(sdcard, "/Download/" + filename1));
            StorageReference ref = storageReference.child(userId).child("bancodados/" + filename1);
            ref.putFile(file1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            StorageReference ref_ = storageReference.child(userId).child("bancodados_"+dataFormatada+"/" + filename1);
            ref_.putFile(file1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(Exportar.this, filename1 + " Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Exportar.this, filename1 + " Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            Log.i("erro", "erro     :    " + e);
        }

    }


    public void exportar(View view) {
            banco.CriaListaParaExporacao(this);
            enviarEmail();
            upload();

    }


    @Override
    public void onBackPressed() {
        //     Intent intent = new Intent(this, TPrincipal.class);
        //   startActivity(intent);
        this.finish();
    }




}
