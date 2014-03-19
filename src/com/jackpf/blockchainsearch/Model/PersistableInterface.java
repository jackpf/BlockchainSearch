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
    /**
     * Context
     */
    protected Context context;
    
    /**
     * Values
     */
    protected TreeMap<String, T> values = new TreeMap<String, T>();
    
    /**
     * Constructor
     * Restores persisted values
     * 
     * @param context
     */
    public PersistableInterface(Context context)
    {
        this.context = context;
        restore();
    }
    
    /**
     * Add a value
     * 
     * @param key
     * @param value
     */
    public void add(String key, T value)
    {
        values.put(key, value);
        save();
    }
    
    /**
     * Remove a value
     * 
     * @param key
     */
    public void remove(String key)
    {
        values.remove(key);
        save();
    }
    
    /**
     * Remove a value by value
     * 
     * @param value
     */
    public void removeByValue(T value)
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
    
    /**
     * Has a value
     * 
     * @param value
     * @return
     */
    public boolean has(String value)
    {
        return values.containsValue(value);
    }
    
    /**
     * Has a key
     * 
     * @param key
     * @return
     */
    public boolean hasKey(String key)
    {
        return values.containsKey(key);
    }
    
    /**
     * Get by key
     * 
     * @param key
     * @return
     */
    public Map.Entry<String, T> get(String key)
    {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            if (key.equals(entry.getKey())) {
                return entry;
            }
        }
        
        return null;
    }
    
    /**
     * Get by value
     * 
     * @param value
     * @return
     */
    public Map.Entry<String, T> getByValue(String value)
    {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry;
            }
        }
        
        return null;
    }
    
    /**
     * Get all entries
     * 
     * @return
     */
    public TreeMap<String, T> getAll()
    {
        return new TreeMap<String, T>(values);
    }
    
    /**
     * Persist entries to file
     */
    public void save()
    {
        try {
            FileOutputStream out = context.openFileOutput(getFilename(), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(out);
            
            os.writeObject(values);
            os.close();
        } catch (FileNotFoundException e) { } catch (IOException e) { }
    }
    
    /**
     * Restore entries from file
     */
    public void restore()
    {
        try {
            FileInputStream in = context.openFileInput(getFilename());
            ObjectInputStream is = new ObjectInputStream(in);
            
            values = (TreeMap<String, T>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) { } catch (IOException e) { } catch (ClassNotFoundException e) { }
    }
    
    /**
     * Abstracted filename
     * 
     * @return
     */
    protected abstract String getFilename();
}
