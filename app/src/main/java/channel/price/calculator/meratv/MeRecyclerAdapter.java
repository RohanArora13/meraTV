package channel.price.calculator.meratv;

import android.content.Context;
import android.graphics.Color;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

//import com.turingtechnologies.materialscrollbar.ICustomAdapter;
//import com.turingtechnologies.materialscrollbar.INameableAdapter;

import com.turingtechnologies.materialscrollbar.ICustomAdapter;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.List;

public class MeRecyclerAdapter extends RecyclerView.Adapter<MeRecyclerAdapter.MeViewHolder>implements Filterable , INameableAdapter, ICustomAdapter {
    private static final String TAG = "MeRecyclerAdapter";

    private Context context;
    private List<SingleItemModel> data;
    private List<SingleItemModel> dataCopy;
    private View.OnClickListener onItemClickListener;

    public MeRecyclerAdapter(Context context, ArrayList<SingleItemModel> data) {
        this.context = context;
        this.data = data;
        dataCopy = new ArrayList<>(data);
    }

    @Override
    public MeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, viewGroup, false);
        return new MeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MeViewHolder holder, int i) {
        final SingleItemModel currentData = data.get(i);
        holder.itemName.setText(currentData.getName());
        holder.itemPrice.setText(String.valueOf(currentData.getPrice()));

        // TODO: change the color here for selected item
        holder.itemView.setBackgroundColor(currentData.isSelected() ? context.getResources().getColor(R.color.green_teal_300) : Color.WHITE);

//        if(currentData.isSelected()){
//            holder.itemCheckBox.setChecked(true);
//        }else {
//            holder.itemCheckBox.setChecked(false);
//        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SingleItemModel> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(dataCopy);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (SingleItemModel item : dataCopy) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                data.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

    }

    @Override
    public Character getCharacterForElement(int element) {
        if (data.size() > 0) {
            // on sort by name(A-Z) scrollbar will show first character of item
            return data.get(element).getName().toUpperCase().charAt(0);
        }
        return Character.MIN_VALUE;
    }

    @Override
    public String getCustomStringForElement(int element) {
        if (data.size() > 0) {
            // on sort by price scrollbar will show price of item
            return String.valueOf(data.get(element).getPrice());
        }
        return "";
    }

    public void setItemClickListener(View.OnClickListener onClickListener) {
        onItemClickListener = onClickListener;
    }

    public class MeViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        CheckBox itemCheckBox;

        public MeViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);

            itemName = itemView.findViewById(R.id.name);
            itemPrice = itemView.findViewById(R.id.price);
            //itemCheckBox = itemView.findViewById(R.id.chk_box);
        }
    }
}
