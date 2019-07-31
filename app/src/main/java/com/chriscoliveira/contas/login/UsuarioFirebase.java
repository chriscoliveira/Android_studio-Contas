package  com.chriscoliveira.contas.login;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsuarioFirebase {

        public static String getIdUser(){

            FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
            String id=Base64Custom.codificarBase64(usuario.getCurrentUser().getEmail());

            return id;
        }

        public static FirebaseUser getUsuarioAtual(){
            FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
            return  usuario.getCurrentUser();
        }



    }

