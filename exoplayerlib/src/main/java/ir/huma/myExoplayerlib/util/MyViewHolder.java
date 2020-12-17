package ir.huma.myExoplayerlib.util;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Created by mr.hamed on 21/11/2015.
 */
public abstract class MyViewHolder<T> {
    private View view;
    private Context context;
    private ArrayAdapter<T> arrayAdapter;
    private Object[] objects;

    public MyViewHolder(Context context, View view, ArrayAdapter<T> arrayAdapter) {
        this.view = view;
        this.context = context;
        this.arrayAdapter = arrayAdapter;
    }

    public abstract void fill(T t, int pos);

    public View getView() {
        return view;
    }

    public Context getContext() {
        return context;
    }

    public ArrayAdapter<T> getArrayAdapter() {
        return arrayAdapter;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}
