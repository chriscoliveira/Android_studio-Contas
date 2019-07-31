package  com.chriscoliveira.contas.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chriscoliveira.contas.MainActivity;
import com.chriscoliveira.contas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoEntrar =  findViewById(R.id.buttonEntrar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    //botao de cadastro de novos usuarios
    public void CadastrarAcesso(View view) {

        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

    }

    //esqueci minha senha
    public void LembrarSenha(View view) {
        String emaildIgitado = campoEmail.getText().toString();
        if (!emaildIgitado.isEmpty()) {

            autenticacao.sendPasswordResetEmail(campoEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Recuperação de senha iniciada. Email enviado.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email incorreto, tente novamente..", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(LoginActivity.this, "Digite o email cadastrado...", Toast.LENGTH_LONG).show();

        }
    }

    //botao de login
    public void logarUsuario(View view) {

        String Semail = campoEmail.getText().toString();
        String Ssenha = campoSenha.getText().toString();
        //botaoEntrar.setVisibility(View.GONE);
        //valida se os campos foram preenchidos
        if (!Semail.isEmpty()) {
            if (!Ssenha.isEmpty()) {

                Usuario usuario = new Usuario();
                usuario.setEmail(Semail);
                usuario.setSenha(Ssenha);
                fazerLogin(usuario);
            } else {
                Snackbar.make(view, "Digite a senha!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                botaoEntrar.setVisibility(View.VISIBLE);
            }
        } else {
            Snackbar.make(view, "Digite o e-mail!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            botaoEntrar.setVisibility(View.VISIBLE);
        }

    }

    public void fazerLogin(Usuario usuario) {

        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    String excessao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excessao = "usuario nao esta cadastrado";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excessao = "Esta conta ja foi cadastrada";
                    } catch (Exception e) {
                        excessao = "Erro: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Snackbar.make(getWindow().getDecorView().getRootView(), excessao, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            }
        });
    }


}