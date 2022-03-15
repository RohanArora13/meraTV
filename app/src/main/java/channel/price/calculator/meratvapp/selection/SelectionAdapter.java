package channel.price.calculator.meratvapp.selection;

import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.design.card.MaterialCardView;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import channel.price.calculator.meratvapp.R;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> implements Filterable {
    private static final String TAG = "SelectionAdapter";

    private Context context;
    private List<Selection> data;
    private List<Selection> dataCopy;
    private View.OnClickListener onItemClickListener;

    private restore_notify listener;

    public interface restore_notify {
        void restore_this(Integer id);
    }

    public SelectionAdapter(Context context, ArrayList<Selection> data) {
        this.context = context;
        this.data = data;
        dataCopy = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_selection, viewGroup, false);

        if (context instanceof restore_notify) {
            listener = (restore_notify) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }

        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int i) {
        // get the data for current view
        final Selection currentData = data.get(i);
        // set name
        holder.nameTV.setText(currentData.getName());
        // set price
        holder.priceTV.setText("Rs. " + String.valueOf(currentData.getTotalPrice()));
        // format timestamp into date
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String date = dateFormat.format(new Date(currentData.getTime()));
        // set date
        holder.dateTV.setText(date);
        // set time
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        String time =timeFormatter.format(new Date(currentData.getTime()));
        time.toLowerCase();
        holder.timeTV.setText(time);
        // check if monthly report is true i.e. > 0
        if (currentData.getMonthlyReport() > 0) {
            // if true highlight the item
         //   holder.cardView.setStrokeColor(context.getResources().getColor(R.color.green_teal_600));
            // show subscriber tv
            holder.subscribedTV.setVisibility(View.VISIBLE);
        } else {
            // otherwise don't highlight it
         //   holder.cardView.setStrokeColor(-1);
            // hide subscriber tv
            holder.subscribedTV.setVisibility(View.INVISIBLE);
        }

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: code for edit button
                Log.d(TAG, "onClick: Edit button clicked");
            }
        });

        holder.restoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Restore button clicked");
                // getting array of string uids
                Log.d(TAG, "onClick: Restore button id = "+ currentData.getSelectionUid());
                listener.restore_this(currentData.getSelectionUid());
                // TODO: code for restore button
            }
        });
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
                List<Selection> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(dataCopy);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Selection item : dataCopy) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(item.getTime());
                        SimpleDateFormat monthDate = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                        String monthName = monthDate.format(cal.getTime());

                        if (monthName.toLowerCase().contains(filterPattern)) {
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

    public void setItemClickListener(View.OnClickListener onClickListener) {
        onItemClickListener = onClickListener;
    }

    public class SelectionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameTV;
        TextView subscribedTV;
        TextView priceTV;
        Button editBtn;
        Button restoreBtn;
        TextView dateTV;
        TextView timeTV;
        ImageButton moreDetailsBtn;

        public SelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);

            cardView = (CardView) itemView;
            nameTV = itemView.findViewById(R.id.selection_name_tv);
            subscribedTV = itemView.findViewById(R.id.selection_subscribed_tv);
            priceTV = itemView.findViewById(R.id.selection_price_tv);
            editBtn = itemView.findViewById(R.id.selection_edit_btn);
            restoreBtn = itemView.findViewById(R.id.selection_restore_btn);
            dateTV = itemView.findViewById(R.id.selection_date_tv);
            moreDetailsBtn = itemView.findViewById(R.id.selection_more_details);
            timeTV = itemView.findViewById(R.id.selection_time_tv);
        }
    }

//    @Override
//    public void onViewAttachedToWindow(@NonNull SelectionViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        if (context instanceof restore_notify) {
//            listener = (restore_notify) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement FragmentAListener");
//        }
//    }

//    @Override
//    public void onViewDetachedFromWindow(@NonNull SelectionViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        listener = null;
//    }

    //    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof restore_notify) {
//            listener = (restore_notify) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement FragmentAListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        listener = null;
//    }
}
