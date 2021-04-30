package com.chinabsc.telemedicine.expert.myAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinabsc.telemedicine.expert.R;
import com.chinabsc.telemedicine.expert.entity.DocMenuData;

import java.util.List;

public class DocMenuDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<DocMenuData> menuDatas;
    private int selectedPos = -1;
    //选中item时的背景颜色
    private int mSelectedBackgroundResource;
    //为选中的背景颜色
    private int mNormalBackgroundResource;
    private boolean hasDivider = true;

    public void setSelectedBackgroundResource(int mSelectedBackgroundResource) {
        this.mSelectedBackgroundResource = mSelectedBackgroundResource;
    }

    public void setNormalBackgroundResource(int mNormalBackgroundResource) {
        this.mNormalBackgroundResource = mNormalBackgroundResource;
    }

    public void setHasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }

    public DocMenuDialogAdapter(Context mContext, List<DocMenuData> menuDatas) {
        this.mContext = mContext;
        this.menuDatas = menuDatas;
    }

    //选中的position,及时更新数据
    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    public void setData(List<DocMenuData> data) {
        this.menuDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (menuDatas == null) {
            return 0;
        }
        return menuDatas.size();
    }

    @Override
    public Object getItem(int position) {
        if (menuDatas == null) {
            return null;
        }
        return menuDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.doc_menu_item, null);
        }
        LinearLayout itemLayout = DocMenuViewHolder.get(convertView, R.id.menu_item_ly);
        TextView nameText = DocMenuViewHolder.get(convertView, R.id.menu_item_textview);
        TextView dividerTextView = DocMenuViewHolder.get(convertView, R.id.menu_item_divider);

        final DocMenuData menuData = menuDatas.get(position);
        //设置标题
        nameText.setText(menuData.name);

        //设置选中时的view
        convertView.setSelected(selectedPos == position);
        nameText.setSelected(selectedPos == position);

        //选中后的标题字体颜色
        nameText.setTextColor(selectedPos == position ? 0xFF00B4C9 : 0xFF333333);
        //选中与未选中的背景色
        if (mNormalBackgroundResource == 0)
            mNormalBackgroundResource = R.color.ffffff;

        if (mSelectedBackgroundResource != 0)
            itemLayout.setBackgroundResource(selectedPos == position ? mSelectedBackgroundResource : mNormalBackgroundResource);

        //隐藏view
        dividerTextView.setVisibility(hasDivider ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }
}
