package br.ufc.quixada.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import br.ufc.quixada.myapplication.model.Usuario;

public class EditPerfilActivity extends AppCompatActivity {
    //comantario teste

    String usuarioId;
    Usuario usuario;
    EditText edit_text_edp_nome;
    EditText edit_text_edp_cpf;
    EditText edit_text_edp_telefone;
    EditText edit_text_edp_email;
    EditText edit_text_edp_senha;
    ImageView image_edp_foto;
    Button btn_edp_edit;
    Button btn_edp_foto;
    Uri selectedUri;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        edit_text_edp_nome = findViewById(R.id.edit_text_edp_nome);
        edit_text_edp_cpf = findViewById(R.id.edit_text_edp_cpf);
        edit_text_edp_telefone = findViewById(R.id.edit_text_edp_telefone);
        edit_text_edp_email = findViewById(R.id.edit_text_edp_email);
        edit_text_edp_senha = findViewById(R.id.edit_text_edp_senha);
        image_edp_foto = findViewById(R.id.image_view_edp_foto);
        btn_edp_edit = findViewById(R.id.btn_edp_edit);
        btn_edp_foto = findViewById(R.id.btn_edp_foto);
        usuarioId = FirebaseAuth.getInstance().getUid();
        Log.i("Teste--", usuarioId);

        btn_edp_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarFoto();
            }
        });

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
                                edit_text_edp_nome.setText(usuario.getNome());
                                edit_text_edp_cpf.setText(usuario.getCpf());
                                edit_text_edp_telefone.setText(usuario.getTelefone());
                                edit_text_edp_email.setText(usuario.getEmail());
                                edit_text_edp_senha.setText(usuario.getSenha());
                                Picasso.get().load(usuario.getFoto()).into(image_edp_foto);
                                btn_edp_foto.setAlpha(0);
                                break;
                            }
                        }
                    }
                });

        btn_edp_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Usuario u = new Usuario(
                        edit_text_edp_nome.getText().toString(),
                        edit_text_edp_cpf.getText().toString(),
                        edit_text_edp_telefone.getText().toString(),
                        edit_text_edp_email.getText().toString(),
                        edit_text_edp_senha.getText().toString()

                );
                updateUser(FirebaseAuth.getInstance().getUid(), u);
            }
        });
    }

    private void updateUser(String srt, Usuario usuario) {
        //pegar os dados do usuario e vefificar o document path
        DocumentReference docRef = firestore.collection("usuarios").document(srt);
        docRef.update("nome", usuario.getNome(), "cpf", usuario.getCpf(), "telefone", usuario.getTelefone(), "email", usuario.getEmail(), "senha", usuario.getSenha());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Intent intent = new Intent(EditPerfilActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            selectedUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                image_edp_foto.setImageDrawable(new BitmapDrawable(bitmap));
                btn_edp_foto.setAlpha(0);
            } catch (IOException e) {

            }
        }
        if(requestCode == 1 && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            image_edp_foto.setImageBitmap(bitmap);
            btn_edp_foto.setAlpha(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selecionarFoto() {
        PopupMenu popup = new PopupMenu(EditPerfilActivity.this, btn_edp_foto);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_camera){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }if (item.getItemId() == R.id.item_galeria){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                }
                return true;
            }
        });
        popup.show();
    }
}