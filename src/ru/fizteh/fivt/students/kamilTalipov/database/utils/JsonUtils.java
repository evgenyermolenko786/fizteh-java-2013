package ru.fizteh.fivt.students.kamilTalipov.database.utils;

import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;

import org.json.JSONObject;

import java.text.ParseException;

import static ru.fizteh.fivt.students.kamilTalipov.database.utils.StoreableUtils.isCorrectStoreable;

public class JsonUtils {
    public static String serialize(Storeable value, Table table) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("Storeable must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (!isCorrectStoreable(value, table)) {
            throw new ColumnFormatException("Incorrect storeable");
        }

        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null) {
                result.append("\"" + i + "\": " + value.getColumnAt(i).toString());
            } else {
                result.append("\"" + i + "\": " + "null");
            }

            if (i != table.getColumnsCount() - 1) {
                result.append(",");
            }  else {
                result.append("}");
            }
        }

        return result.toString();
    }

    public static Storeable deserialize(String value,
                                        TableProvider provider, Table table) throws ParseException {
        if (provider == null) {
            throw new IllegalArgumentException("Table provider must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (value == null) {
            return null;
        }

        JSONObject json;
        try {
            json = new JSONObject(value);
        } catch (JSONException e) {
            ParseException exception = new ParseException("JSON: incorrect format", 0);
            exception.addSuppressed(e);
            throw exception;
        }
        Storeable result = provider.createFor(table);
        for (int i = 0; i < json.length(); ++i) {
            try {
                Object object;
                try {
                    object = json.get(Integer.toString(i));
                } catch (JSONException e) {
                    ParseException exception = new ParseException("JSON: incorrect format", i);
                    exception.addSuppressed(e);
                    throw exception;
                }
                if (object == JSONObject.NULL) {
                    result.setColumnAt(i, null);
                } else {
                    if (table.getColumnType(i) == Long.class) {
                        result.setColumnAt(i, Long.valueOf(object.toString()));
                    } else {
                        result.setColumnAt(i, table.getColumnType(i).cast(object));
                    }
                }
            }  catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException("JSON: incorrect format", i);
            }
        }

        return result;
    }
}
