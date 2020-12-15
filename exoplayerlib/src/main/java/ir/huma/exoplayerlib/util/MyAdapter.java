package ir.huma.exoplayerlib.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Hamed Ghayour on 21/11/2015.
 */
public class MyAdapter<T> extends ArrayAdapter<T> {

    private Class<? extends MyViewHolder> Holder;
    private int res;
    private Context context;
    private Object[] objects;
    private LayoutInflater inflater;


    /**
     * @param context
     * @param resource address of resouce in layout file
     * @param holder   you must write class extend MyViewHolder and pass the class here
     */
    public MyAdapter(Context context, int resource, Class<? extends MyViewHolder> holder) {
        super(context, resource);
        this.context = context;
        myInit(holder, resource);
    }

    /**
     * @param context
     * @param resource address of resouce in layout file
     * @param items    items you want see in your list
     * @param holder   you must write class extend MyViewHolder and pass the class here
     */
    public MyAdapter(Context context, int resource, Class<? extends MyViewHolder> holder, List<T> items) {
        super(context, resource, items);
        this.context = context;
        myInit(holder, resource);

    }

    /**
     * @param context
     * @param resource address of resouce in layout file
     * @param items    items you want see in your list
     * @param holder   you must write class extend MyViewHolder and pass the class here
     */
    public MyAdapter(Context context, int resource, Class<? extends MyViewHolder> holder, T[] items) {
        super(context, resource, items);
        this.context = context;
        myInit(holder, resource);
    }

    private void myInit(Class<? extends MyViewHolder> holder, int res) {
        this.Holder = holder;
        this.res = res;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * if you want pass data to fill method, call this method and pass it anything you want
     * you must cast this objects in fill method to waht ever you like
     *
     * @param objects anything you want
     */
    public void setObjects(Object... objects) {
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(res, parent, false);
            try {
                h = Holder.getConstructor(Context.class, View.class, ArrayAdapter.class).newInstance(context, convertView, this);
                h.setObjects(objects);
            } catch (Exception e) {
                e.printStackTrace();
                return convertView;
            }
            //FontManager.instance().setTypeface(convertView);
            convertView.setTag(h);
        } else {
            h = (MyViewHolder) convertView.getTag();
        }
        h.fill(getItem(position), position);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
