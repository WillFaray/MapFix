package com.example.ecoufrr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OcorrenciaAdapter extends RecyclerView.Adapter<OcorrenciaAdapter.ViewHolder> {
    private List<Ocorrencia> ocorrencias;
    private Context context;
    private OnOcorrenciaActionListener listener;

    public interface OnOcorrenciaActionListener {
        void onVerClick(Ocorrencia ocorrencia);
        void onEditarClick(Ocorrencia ocorrencia);
        void onDeletarClick(int id);
    }

    public OcorrenciaAdapter(Context context, List<Ocorrencia> ocorrencias,
                             OnOcorrenciaActionListener listener) {
        this.context = context;
        this.ocorrencias = ocorrencias;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ocorrencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ocorrencia ocorrencia = ocorrencias.get(position);
        holder.tvTitulo.setText(ocorrencia.getTitulo());
        holder.tvLocalizacao.setText("📍 " + ocorrencia.getLocalizacao());
        holder.tvStatus.setText("Status: " + ocorrencia.getStatus());

        holder.btnVer.setOnClickListener(v -> listener.onVerClick(ocorrencia));
        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(ocorrencia));
        holder.btnDeletar.setOnClickListener(v -> listener.onDeletarClick(ocorrencia.getId()));
    }

    @Override
    public int getItemCount() {
        return ocorrencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvLocalizacao, tvStatus;
        Button btnVer, btnEditar, btnDeletar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvLocalizacao = itemView.findViewById(R.id.tvLocalizacao);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnVer = itemView.findViewById(R.id.btnVer);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnDeletar = itemView.findViewById(R.id.btnDeletar);
        }
    }
}