package cn.ittiger.loading.demo;

import cn.ittiger.loading.demo.view.HeaderAndFooterAdapter;
import cn.ittiger.loading.demo.view.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * laohu
 */
public class StringListAdapter extends HeaderAndFooterAdapter<String> {

    private Context mContext;

    public StringListAdapter(Context context, List<String> list) {

        super(list);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, String item) {

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.mTextView.setText(item);
    }

    public class ItemViewHolder extends ViewHolder {

        public TextView mTextView;

        public ItemViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
