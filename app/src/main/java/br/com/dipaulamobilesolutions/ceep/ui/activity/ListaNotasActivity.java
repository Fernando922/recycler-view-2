package br.com.dipaulamobilesolutions.ceep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.dipaulamobilesolutions.ceep.R;
import br.com.dipaulamobilesolutions.ceep.dao.NotaDAO;
import br.com.dipaulamobilesolutions.ceep.model.Nota;
import br.com.dipaulamobilesolutions.ceep.ui.recyclerview.adapter.ListaNotasAdapter;
import br.com.dipaulamobilesolutions.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;
import br.com.dipaulamobilesolutions.ceep.ui.recyclerview.helper.callback.NotaItemTouchHelperCallback;

import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICA_ALTERA_NOTA;
import static br.com.dipaulamobilesolutions.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

public class ListaNotasActivity extends AppCompatActivity {

    public static final String NOTAS = "Notas";
    private ListaNotasAdapter adapter;
    private NotaDAO notaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        setTitle(NOTAS);


        TextView botaoInsereNota = pegaTodasNotas();


        configuraBotaoInsereNota(botaoInsereNota);

        List<Nota> todasNotas = notaDAO.todos();
        configuraRecyclerView(todasNotas);


    }

    private void configuraBotaoInsereNota(TextView botaoInsereNota) {
        botaoInsereNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaiParaFormularioNotaActivityInsere();
            }
        });
    }

    private void vaiParaFormularioNotaActivityInsere() {
        startActivityForResult(new Intent(ListaNotasActivity.this, FormularioNotaActivity.class), CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private TextView pegaTodasNotas() {
        TextView botaoInsereNota = findViewById(R.id.lista_notas_insere_nota);
        notaDAO = new NotaDAO();
        return botaoInsereNota;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ehResultadoInsereNota(requestCode, data)) {
            if (resultadoOK(resultCode)) {
                Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
                adicionaNota(notaRecebida);
            }

        }

        if (ehResultadoAlteraNota(requestCode, data)) {
            if (resultadoOK(resultCode)) {
                Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
                int posicaoRecebida = data.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);
                if (ehPosicaoValida(posicaoRecebida)) {
                    NotaDAO notaDAO = new NotaDAO();
                    altera(notaRecebida, posicaoRecebida, notaDAO);
                } else {
                    Toast.makeText(this, "Ocorreu um problema na alteração da nota", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void altera(Nota nota, int posicao, NotaDAO notaDAO) {
        notaDAO.altera(posicao, nota);
        adapter.altera(posicao, nota);
    }

    private boolean ehPosicaoValida(int posicaoRecebida) {
        return posicaoRecebida > POSICAO_INVALIDA;
    }

    private boolean ehResultadoAlteraNota(int requestCode, @Nullable Intent data) {
        return ehCodigoRequisicaoAlteraNota(requestCode) && temNota(data);
    }

    private boolean ehCodigoRequisicaoAlteraNota(int requestCode) {
        return requestCode == CODIGO_REQUISICA_ALTERA_NOTA;
    }

    private void adicionaNota(Nota notaRecebida) {
        notaDAO.insere(notaRecebida);
        adapter.adiciona(notaRecebida);
    }

    private boolean ehResultadoInsereNota(int requestCode, @Nullable Intent data) {
        return ehCodigoRequisicaoInsereNota(requestCode) && temNota(data);
    }

    private boolean temNota(@Nullable Intent data) {
        return data != null && data.hasExtra(CHAVE_NOTA);
    }

    private boolean resultadoOK(int resultCode) {
        return resultCode == Activity.RESULT_OK;
    }

    private boolean ehCodigoRequisicaoInsereNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_INSERE_NOTA;
    }


    private void configuraRecyclerView(List<Nota> todasNotas) {
        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todasNotas, listaNotas);
        configuraLayoutManager(listaNotas);
        configuraItemTouchHelper(listaNotas);
    }

    private void configuraItemTouchHelper(RecyclerView listaNotas) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NotaItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listaNotas);
    }


    private void configuraLayoutManager(RecyclerView listaNotas) {
        //LinearLayoutManager ou GridLayoutManager ou StaggeredGridLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listaNotas.setLayoutManager(linearLayoutManager);
    }

    private void configuraAdapter(List<Nota> todos, RecyclerView listaNotas) {
        adapter = new ListaNotasAdapter(this, todos);
        listaNotas.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Nota nota, int posicao) {
                vaiParaFormularioNotaActivityAltera(nota, posicao);
            }
        });
    }

    private void vaiParaFormularioNotaActivityAltera(Nota nota, int posicao) {
        Intent abreFormularioComNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
        abreFormularioComNota.putExtra(CHAVE_POSICAO, posicao);
        startActivityForResult(abreFormularioComNota, CODIGO_REQUISICA_ALTERA_NOTA);
    }
}