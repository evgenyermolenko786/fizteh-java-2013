package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

import java.io.IOException;

public class RemoveCommand implements Command {
    @Override
    public boolean exec(State state, String[] command) {
        if (command.length != 2) {
            System.err.println("remove: Usage - remove key");
            return false;
        }
        try {
            FileMap db = null;
            try {
                db = (FileMap) state.getMyState(command[1].hashCode());
            } catch (IOException e) {
                if (e.getMessage().equals("no table")) {
                    System.out.println("no table");
                    return false;
                }
                System.err.println(e.getMessage());
            }
            if (db == null) {
                System.out.println("not found");
                return true;
            }
            String str = db.remove(command[1]);
            if (db.isEmpty()) {
                ((FileMapTable) state).deleteFileMap(command[1].hashCode());
            }
            if (str.equals("not found")) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (Exception e) {
            System.err.println("remove: Can't remove the key");
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "remove";
    }
}
