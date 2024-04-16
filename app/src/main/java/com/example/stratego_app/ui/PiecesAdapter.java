package com.example.stratego_app.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stratego_app.R;

import java.util.List;


public class PiecesAdapter extends RecyclerView.Adapter<PiecesAdapter.PieceViewHolder> {
    private List<String> pieces;

    public PiecesAdapter(List<String> pieces) {
        this.pieces = pieces;
    }

    @NonNull
    @Override
    public PieceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pieces, parent, false);
        return new PieceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PieceViewHolder holder, int position) {
        String pieceType = pieces.get(position); // e.g., "bomb", "flag", etc.
        Context context = holder.imageView.getContext();
        int drawableId = context.getResources().getIdentifier(pieceType, "drawable", context.getPackageName());

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        String description = context.getString(context.getResources().getIdentifier(
                "piece_" + pieceType + "_desc", "string", context.getPackageName()));

        holder.imageView.setImageDrawable(drawable);
        holder.imageView.setContentDescription(description);
        holder.nameTextView.setText(pieceType);
    }

    @Override
    public int getItemCount() {
        return pieces.size();
    }

    public static class PieceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public PieceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pieceImageView);
            nameTextView = itemView.findViewById(R.id.pieceNameTextView);  // Connect the TextView
        }
    }
}

