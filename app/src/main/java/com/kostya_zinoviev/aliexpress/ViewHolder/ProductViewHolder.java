package com.kostya_zinoviev.aliexpress.ViewHolder;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kostya_zinoviev.aliexpress.Interface.ItemClickE;
import com.kostya_zinoviev.aliexpress.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView nameProduct,desProduct,priceProduct;
    public ImageView imageProduct;
    public ItemClickE listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        nameProduct = itemView.findViewById(R.id.product_name);
        desProduct = itemView.findViewById(R.id.product_desc);
        imageProduct = itemView.findViewById(R.id.image_product);
        priceProduct = itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListener(ItemClickE listener){
        this.listener = listener;
    }


    //Мы создали наш собвственный интерфейс,чтобы обрабатывать нажатия по позиции,которую мы получаем
    //С помощью getAdapter() при отнклике на ItemView ,передавая v  в параметр метода нашего интерфейса
    //Так мы можем обрабатывать клики по позиции))
    @Override
    public void onClick(View v) {
        listener.itemClick(v,getAdapterPosition(),false);
    }
}
