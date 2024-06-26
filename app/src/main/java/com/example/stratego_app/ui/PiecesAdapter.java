package com.example.stratego_app.ui;

import android.content.ClipData;
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
import com.example.stratego_app.model.Piece;

import java.util.ArrayList;
import java.util.List;


public class PiecesAdapter extends RecyclerView.Adapter<PiecesAdapter.PieceViewHolder> {
    private List<Piece> pieces;
    private final OnPieceDragListener dragListener;

    public PiecesAdapter(List<Piece> pieces, OnPieceDragListener dragListener) {
        this.pieces = pieces;
        this.dragListener = dragListener;
    }


    @NonNull
    @Override
    public PieceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pieces, parent, false);
        return new PieceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PieceViewHolder holder, int position) {
        Piece piece = pieces.get(position);
        String pieceType = piece.getRank().name();
        Context context = holder.imageView.getContext();

        int drawableId = context.getResources().getIdentifier(pieceType.toLowerCase(), "drawable", context.getPackageName());

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        holder.imageView.setImageDrawable(drawable);
        holder.nameTextView.setText(pieceType.toUpperCase());

        /*Optionally display rank and ID
        holder.rankTextView.setText(piece.getRank().toString());*/

        // Set long click listener to start drag from the ImageView only
        holder.imageView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("pieceType", pieceType);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, piece, 0);
            if (dragListener != null) {
                dragListener.onStartDrag(holder, position);
            }
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return pieces.size();
    }

    // Method to remove an item from the adapter
    public void removeItem(int position) {
        if (position >= 0 && position < pieces.size()) {
            pieces.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clearPieces() {
        pieces.clear(); // Clear the list of pieces
    }

    public void setPieces(List<Piece> pieces) {
        this.pieces = new ArrayList<>(pieces);
    }

    //------- Drag and Drop -----
    public static class PieceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public PieceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pieceImageView);
            nameTextView = itemView.findViewById(R.id.pieceNameTextView);
            //rankTextView = itemView.findViewById(R.id.pieceRankTextView); // Ensure these IDs are in your XML
        }
    }

    public interface OnPieceDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder, int position);
    }
}

