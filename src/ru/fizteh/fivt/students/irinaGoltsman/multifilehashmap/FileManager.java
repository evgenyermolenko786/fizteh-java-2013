package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.tools.ColumnTypes;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class FileManager {

    private static class LineOfDB {
        public long length = 0;
        public String key = "";
        public String value = "";

        public LineOfDB() {
        }

        public LineOfDB(long length, String key, String value) {
            this.length = length;
            this.key = key;
            this.value = value;
        }
    }

    private static LineOfDB readLineOfDatFile(RandomAccessFile datFile, long inputLength) throws IOException {
        long length = inputLength;
        int lengthOfKey;
        int lengthOfValue;
        LineOfDB empty = new LineOfDB();
        try {
            lengthOfKey = datFile.readInt();
            lengthOfValue = datFile.readInt();
        } catch (IOException e) {
            throw new IOException("Wrong format of db: " + e.getMessage());
        }
        length -= 8;
        if (lengthOfKey <= 0 || lengthOfValue <= 0) {
            throw new IOException("Wrong format of db: length of key and length of value must be positive integers.");
        }
        if (lengthOfKey > length) {
            throw new IOException("Wrong format of db: length of key ​​do not match content.");
        }
        byte[] bytesOfKey = readKeyOrValue(datFile, lengthOfKey);
        length -= lengthOfKey;
        if (lengthOfValue > length) {
            System.err.println("Wrong format of db: length of value ​​do not match content.");
            return empty;
        }
        byte[] bytesOfValue = readKeyOrValue(datFile, lengthOfValue);
        length -= lengthOfValue;
        String key;
        String value;
        try {
            key = new String(bytesOfKey, "UTF-8");
            value = new String(bytesOfValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
            return empty;
        }
        long lengthOfLine = inputLength - length;
        LineOfDB result = new LineOfDB(lengthOfLine, key, value);
        return result;
    }

    private static byte[] readKeyOrValue(RandomAccessFile datFile, int length) throws IOException {
        byte[] bytes = new byte[length];
        int countOfBytesWasRead = 0;
        while (true) {
            try {
                countOfBytesWasRead = datFile.read(bytes, countOfBytesWasRead, length - countOfBytesWasRead);
            } catch (IOException e) {
                throw new IOException("Wrong format of db: " + e.getMessage());
            }
            if (countOfBytesWasRead == length) {
                break;
            }
            if (countOfBytesWasRead == -1) {
                throw new IOException("Error while reading key or value");
            }
        }
        return bytes;
    }

    private static void readFromDisk(RandomAccessFile datFile, HashMap<String, String> storage) throws IOException {
        long length = -1;
        length = datFile.length();
        if (length == 0) {
            return;
        }
        while (length > 0) {
            LineOfDB line = readLineOfDatFile(datFile, length);
            length -= line.length;
            storage.put(line.key, line.value);
        }
        datFile.close();
    }

    public static void readDBFromDisk(File tableDirectory, HashMap<String, String> tableStorage) throws IOException {
        if (!tableDirectory.exists() || tableDirectory.isFile()) {
            throw new IOException(tableDirectory + ": not directory or not exist");
        }
        for (int index = 0; index < 16; index++) {
            String currentDirectoryName = index + ".dir";
            File currentDirectory = new File(tableDirectory, currentDirectoryName);
            if (!currentDirectory.exists()) {
                continue;
            }
            if (!currentDirectory.isDirectory()) {
                throw new IOException(currentDirectory.toString() + ": not directory");
            }
            for (int fileIndex = 0; fileIndex < 16; fileIndex++) {
                String currentFileName = fileIndex + ".dat";
                File currentFile = new File(currentDirectory, currentFileName);
                if (!currentFile.exists()) {
                    continue;
                }
                RandomAccessFile fileIndexDat = null;
                try {
                    fileIndexDat = new RandomAccessFile(currentFile, "rw");
                } catch (FileNotFoundException e) {
                    continue;
                }
                readFromDisk(fileIndexDat, tableStorage);
            }
        }
    }

    private static void cleanEmptyDir(File dir) throws IOException {
        if (dir.exists()) {
            if (dir.listFiles().length == 0) {
                if (!dir.delete()) {
                    throw new IOException("File: " + dir.toString() + " can't be deleted");
                }
            }
        }
    }

    private static void parseStorage(HashMap<String, String>[][] parsedStorage, HashMap<String, String> storage) {
        for (String key : storage.keySet()) {
            int hashCode = key.hashCode();
            int indexOfDir = hashCode % 16;
            if (indexOfDir < 0) {
                indexOfDir *= -1;
            }
            int indexOfDat = hashCode / 16 % 16;
            if (indexOfDat < 0) {
                indexOfDat *= -1;
            }
            if (parsedStorage[indexOfDir][indexOfDat] == null) {
                parsedStorage[indexOfDir][indexOfDat] = new HashMap<>();
            }
            parsedStorage[indexOfDir][indexOfDat].put(key, storage.get(key));
        }
    }

    public static void writeToDatFile(RandomAccessFile datFile, HashMap<String, String> storage) throws IOException {
        try {
            datFile.seek(0);
            datFile.setLength(0);
            for (String key : storage.keySet()) {
                byte[] bytesOfKey = key.getBytes("UTF-8");
                byte[] bytesOfValue = storage.get(key).getBytes("UTF-8");
                datFile.writeInt(bytesOfKey.length);
                datFile.writeInt(bytesOfValue.length);
                datFile.write(bytesOfKey);
                datFile.write(bytesOfValue);
            }
        } catch (Exception e) {
            try {
                datFile.close();
            } catch (Exception e2) {
                throw new IOException(e + "\n" + e2);
            }
        }
    }

    public static void writeTableOnDisk(File tableDirectory, HashMap<String, String> tableStorage) throws IOException {
        if (tableDirectory == null) {
            throw new IOException("Error! You try to write in null file.");
        }
        HashMap<String, String>[][] parsedStorage = new HashMap[16][16];
        parseStorage(parsedStorage, tableStorage);
        for (int indexOfDir = 0; indexOfDir < 16; indexOfDir++) {
            File dir = new File(tableDirectory, indexOfDir + ".dir");
            for (int indexOfDatFile = 0; indexOfDatFile < 16; indexOfDatFile++) {
                File datFile = new File(dir, indexOfDatFile + ".dat");
                if (parsedStorage[indexOfDir][indexOfDatFile] == null) {
                    if (datFile.exists()) {
                        if (!datFile.delete()) {
                            throw new IOException("File: " + datFile.toString() + " can't be deleted");
                        }
                    }
                    continue;
                }
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        throw new IOException("Directory  " + dir.toString() + " can't be created");
                    }
                }
                if (!datFile.exists()) {
                    if (!datFile.createNewFile()) {
                        throw new IOException("File " + datFile.toString() + " can't be created");
                    }
                }
                RandomAccessFile currentFile = null;
                try {
                    currentFile = new RandomAccessFile(datFile, "rw");
                } catch (FileNotFoundException e) {
                    throw new IOException("This error is my fail. Check 'writeTableOnDisk' function");
                }
                writeToDatFile(currentFile, parsedStorage[indexOfDir][indexOfDatFile]);
                try {
                    currentFile.close();
                } catch (IOException e) {
                    throw new IOException("File " + currentFile.toString() + " can't be closed.");
                }
            }
            cleanEmptyDir(dir);
        }
    }

    public static List<Class<?>> readTableSignature(File tableDirectory) throws IOException {
        if (!tableDirectory.exists()) {
            throw new IllegalArgumentException("not existed table");
        }
        File signature = new File(tableDirectory, "signature.tsv");
        if (!signature.exists()) {
            throw new IOException("signature file not exist: " + signature.getCanonicalPath());
        }
        Scanner scan = new Scanner(signature);
        if (!scan.hasNext()) {
            throw new IOException("empty signature: " + signature.getCanonicalPath());
        }
        String[] types = scan.next().split(" ");
        ColumnTypes columnTypes = new ColumnTypes();
        List<Class<?>> listOfTypes = columnTypes.convertArrayOfStringsToListOfClasses(types);
        scan.close();
        return listOfTypes;
    }

    public static void writeSignature(File tableDirectory, List<String> columnTypes) throws IOException {
        if (!tableDirectory.exists()) {
            throw new IllegalArgumentException("not existed table");
        }
        if (columnTypes == null || columnTypes.size() == 0) {
            throw new IllegalArgumentException("null or empty list of column types");
        }
        File signature = new File(tableDirectory, "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IOException("failed to create new signature.tsv: probably a file with such name already exists");
        }
        RandomAccessFile signatureFile = new RandomAccessFile(signature, "rw");
        for (String type : columnTypes) {
            //TODO: роверь работу signatureFile.writeUTF(type)
            signatureFile.writeUTF(type);
            signatureFile.write(' ');
        }
        signatureFile.close();
    }
}
