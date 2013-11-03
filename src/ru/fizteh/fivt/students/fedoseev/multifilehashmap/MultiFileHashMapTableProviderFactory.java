package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {
    @Override
    public MultiFileHashMapTableProvider create(String dirName) throws IOException {
        if (dirName == null || dirName.equals("")) {
            throw new IllegalArgumentException("CREATE ERROR: incorrect directory name");
        }
        if (!new File(dirName).isDirectory()) {
            throw new IllegalArgumentException("CREATE ERROR: table is not directory");
        }

        return new MultiFileHashMapTableProvider();
    }
}
