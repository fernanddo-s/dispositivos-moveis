package br.ufc.quixada.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.ufc.quixada.myapplication.model.Usuario;

public class PerfilActivity extends AppCompatActivity {
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    String usuarioId;
    List<Usuario> usuarios = new ArrayList();
    Usuario usuario;
    TextView edit_text_pf_nome;
    TextView edit_text_pf_cpf;
    TextView edit_text_pf_telefone;
    TextView edit_text_pf_email;
    TextView edit_text_pf_senha;
    ImageView image_pf_foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        edit_text_pf_nome = findViewById(R.id.edit_text_pf_nome);
        edit_text_pf_cpf = findViewById(R.id.edit_text_pf_cpf);
        edit_text_pf_telefone = findViewById(R.id.edit_text_pf_telefone);
        edit_text_pf_email = findViewById(R.id.edit_text_pf_email);
        edit_text_pf_senha = findViewById(R.id.edit_text_pf_senha);
        //image_pf_foto = findViewById(R.id.image_view_pf_foto);
        usuarioId = FirebaseAuth.getInstance().getUid();
        Log.i("Teste--", usuarioId);
        FirebaseFirestore.getInstance().collection("/usuarios")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Teste", error.getMessage(), error);
                            return;
                        }
                        List<DocumentSnapshot> documents = value.getDocuments();
                        for (DocumentSnapshot doc : documents) {
                            usuario = doc.toObject(Usuario.class);
                            if (usuario.getUuid().equals(usuarioId)) {
                                edit_text_pf_nome.setText(usuario.getNome());
                                edit_text_pf_cpf.setText(usuario.getCpf());
                                edit_text_pf_telefone.setText(usuario.getTelefone());
                                edit_text_pf_email.setText(usuario.getEmail());
                                edit_text_pf_senha.setText(usuario.getSenha());
                                edit_text_pf_senha.setText(usuario.getFoto());
                                Log.d("Teste--", usuario.getCpf());
                            }
                            Log.d("Teste--", usuario.getNome());
                        }
                    }
                });
    }
}

