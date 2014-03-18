package com.jackpf.blockchainsearch.Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

public abstract class PersistableInterface<T>
{
    protected Context context;
    
    protected TreeMap<String, T> values = new TreeMap<String, T>();
    
    public PersistableInterface(Context context)
    {
        this.context = context;
        
        restore();
    }
    
    public void add(String key, T value)
    {
        values.put(key, value);
        save();
    }
    
    public void removeByKey(String key)
    {
        values.remove(key);
        save();
    }
    
    public void remove(T value)
    {
        Iterator <Map.Entry<String, T>> iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, T> entry = iterator.next();
            if (value.equals(entry.getValue())) {
                iterator.remove();
            }
        }
        save();
    }
    
    public boolean has(String value)
    {
        return values.containsValue(value);
    }
    
    public boolean hasKey(String key)
    {
        return values.containsKey(key);
    }
    
    public Map.Entry<String, T> get(String value)
    {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry;
            }
        }
        
        return null;
    }
    
    public Map.Entry<String, T> getByKey(String key)
    {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            if (key.equals(entry.getKey())) {
                return entry;
            }
        }
        
        return null;
    }
    
    public TreeMap<String, T> getAll()
    {
        return new TreeMap<String, T>(values);
    }
    
    protected void save()
    {
        try {
            FileOutputStream out = context.openFileOutput(getFilename(), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(out);
            
            os.writeObject(values);
            os.close();
        } catch (FileNotFoundException e) { } catch (IOException e) { }
    }
    
    protected void restore()
    {
        try {
            FileInputStream in = context.openFileInput(getFilename());
            ObjectInputStream is = new ObjectInputStream(in);
            
            values = (TreeMap<String, T>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) { } catch (IOException e) { } catch (ClassNotFoundException e) { }
    }
    
    protected abstract String getFilename();
}
