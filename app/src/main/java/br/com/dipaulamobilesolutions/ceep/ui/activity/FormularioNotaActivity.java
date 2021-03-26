package br.com.dipaulamobilesolutions.ceep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import br.com.dipaulamobilesolutions.ceep.R;
import br.com.dipaulamobilesolutions.ceep.model.Nota;

import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

public class FormularioNotaActivity extends AppCompatActivity {

    private static final String NOVA_NOTA = "Nova Nota";
    private static final String ALTERA_NOTA = "Altera nota";
    private int posicaoRecebida = POSICAO_INVALIDA;
    private EditText titulo;
    private EditText descricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_nota);
        setTitle(NOVA_NOTA);
        inicializacaoDosCampos();


        verificaSeEhUmaEdicao();
    }

    private void verificaSeEhUmaEdicao() {
        Intent intent = getIntent();
        if (intent.hasExtra(CHAVE_NOTA)) {
            setTitle(ALTERA_NOTA);
            Nota notaRecebida = (Nota) intent.getSerializableExtra(CHAVE_NOTA);
            posicaoRecebida = intent.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);  // nao é necessario verificar no if, ja que um valor default é fornecido
            preencheCampos(notaRecebida);
        }
    }

    private void preencheCampos(Nota notaRecebida) {
        titulo.setText(notaRecebida.getTitulo());
        descricao.setText(notaRecebida.getDescricao());
    }

    private void inicializacaoDosCampos() {
        titulo = findViewById(R.id.formulario_nota_titulo);
        descricao = findViewById(R.id.formulario_nota_descricao);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (ehMenuSalvaNota(item)) {
            Nota novaNota = criarNota();
            retornaNotaParaLista(novaNota);
        }

        return super.onOptionsItemSelected(item);
    }

    private void retornaNotaParaLista(Nota novaNota) {
        Intent resultadoInsercao = new Intent();
        resultadoInsercao.putExtra(CHAVE_NOTA, novaNota);
        resultadoInsercao.putExtra(CHAVE_POSICAO, posicaoRecebida);
        setResult(Activity.RESULT_OK, resultadoInsercao);
        finish();
    }

    private Nota criarNota() {
        return new Nota(titulo.getText().toString(), descricao.getText().toString());
    }

    private boolean ehMenuSalvaNota(@NonNull MenuItem item) {
        return item.getItemId() == R.id.menu_formulario_nota_ic_salva;
    }


}