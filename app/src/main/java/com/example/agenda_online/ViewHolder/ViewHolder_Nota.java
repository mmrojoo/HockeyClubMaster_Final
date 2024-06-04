package com.example.agenda_online.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agenda_online.R;

public class ViewHolder_Nota extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolder_Nota.ClickListener mClickListener

    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder_Nota.ClickListener clicklistener) {
        mClickListener = clicklistener;
    }

    public ViewHolder_Nota(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
    }

    public void SetearDatos(Context context, String uid_usuario, String correo_usuario
            , String fecha_hora_registro, String titulo, String descripcion, String fecha_nota
            , String estado){

        //DECLARAR LAS VISTAS
        TextView Id_nota_Item, Uid_Usuario_Item, Correo_usuario_Item, Fecha_hora_registro_Item,
                Titulo_Item, Descripcion_Item, Fecha_Item, Estado_Item;


        Id_nota_Item = mView.findViewById(R.id.Id_nota_item);
        Uid_Usuario_Item = mView.findViewById(R.id.Uid_Usuario_Item);
        Correo_usuario_Item = mView.findViewById(R.id.Correo_usuario_Item);
        Fecha_hora_registro_Item = mView.findViewById(R.id.Fecha_hora_registro_Item);
        Titulo_Item = mView.findViewById(R.id.Id_nota_item);
        Descripcion_Item = mView.findViewById(R.id.Id_nota_item);
        Fecha_Item = mView.findViewById(R.id.Id_nota_item);
        Estado_Item = mView.findViewById(R.id.Id_nota_item);
    }
}