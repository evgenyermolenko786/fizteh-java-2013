package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiFileHashMapTable implements Table {

    private Map<String, String> dataBase;
    private Map<String, String> changesBase;
    public File dataFile;

    public MultiFileHashMapTable(File currentFile) {

        dataBase = new HashMap<String, String>();
        changesBase = new HashMap<String, String>();
        dataFile = currentFile;
    }

    public Map<String, String> getDataBase() {

        return dataBase;
    }

    public int getChangesBaseSize() {

        return changesBase.size();
    }

    public Boolean checkChangesBase(String name) {

        return changesBase.containsKey(name);
    }

    public String getFromChangesBase(String key) {

        return changesBase.get(key);
    }

    public void putToChangesBase(String key, String value) {

        changesBase.put(key, value);
    }

    public void removeFromChangesBase(String key) {

        changesBase.remove(key);
    }

    public File getDataFile() {

        return dataFile;
    }

    public void setDataBase(Map<String, String> inDataBase) {

        dataBase = inDataBase;
    }

    public void setDataFile(File inDataFile) {

        dataFile = inDataFile;
    }

    @Override
    public String getName() {

        return dataFile.getName();
    }

    @Override
    public String get(String key) {

        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to get");
        }
        String newKey = key.trim();
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key to get");
        }
        return dataBase.get(newKey);
    }

    @Override
    public String put(String key, String value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key or value to put");
        }
        String newKey = key.trim();
        String newValue = value.trim();
        if (newKey.isEmpty() || newValue.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key or value to put");
        }
        return changesBase.put(newKey, newValue);
    }

    @Override
    public String remove(String key) {

        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove");
        }
        String newKey = key.trim();
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key to remove");
        }
        return dataBase.remove(newKey);
    }

    @Override
    public int size() {

        int size = dataBase.size();

        Set<Map.Entry<String, String>> set = changesBase.entrySet();
        for (Map.Entry<String, String> pair : set) {
            pair.getKey();
            if (pair.getValue() == null) {
                --size;
            } else {
                ++size;
            }
        }
        return size;
    }

    @Override
    public int commit() {

        int size = changesBase.size();
        try {
            if (size != 0) {
                Set<Map.Entry<String, String>> set = changesBase.entrySet();
                for (Map.Entry<String, String> pair : set) {
                    pair.getKey();
                    if (pair.getValue() == null) {
                        dataBase.remove(pair.getKey());
                    } else {
                        dataBase.put(pair.getKey(), pair.getValue());
                    }
                }
                MultiFileHashMapUtils.write(dataFile, dataBase);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        this.changesBase.clear();
        return size;
    }

    @Override
    public int rollback() {

        int size = changesBase.size();
        changesBase.clear();
        return size;
    }
}