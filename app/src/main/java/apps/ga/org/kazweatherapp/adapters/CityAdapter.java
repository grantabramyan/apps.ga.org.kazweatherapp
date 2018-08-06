package apps.ga.org.kazweatherapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.ga.org.kazweatherapp.R;
import apps.ga.org.kazweatherapp.domain.City;

/**
 * Created by grant on 03,August,2018
 */
public class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<City> data;
    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener{
        void onItemClick(City city, int position);
    }

    private CityAdapter() {

    }

    public CityAdapter(Context context, List<City> data, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.city_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        City city=data.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.title.setText(city.getName());
        viewHolder.temperature.setText(city.getWeather()==null ? "нет данных" : city.getWeather().getMainData()==null ? "нет данных" : String.format("%.0f",city.getWeather().getMainData().getTemperature()));
        viewHolder.bind(city,position,onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView temperature;

        public ViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.city_title);
            temperature=itemView.findViewById(R.id.temperature);
        }

        void bind(City t,int position,OnItemClickListener onItemClickListener){
            itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(t,position);
            });
        }
    }

    public List<City> getData() {
        return data;
    }

    public void setData(List<City> data) {
        this.data = data;
    }
}
