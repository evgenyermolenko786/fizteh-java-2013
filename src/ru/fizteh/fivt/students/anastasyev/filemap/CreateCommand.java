package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;
import java.io.IOException;

public class CreateCommand implements Command {
    @Override
    public boolean exec(State state, String[] command) {
        if (command.length != 2) {
            System.err.println("create: Usage - create tablename");
            return false;
        }
        try {
            ((FileMapTable) state).createTable(command[1]);
        } catch (IOException e) {
            System.err.println("create: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "create";
    }
}
