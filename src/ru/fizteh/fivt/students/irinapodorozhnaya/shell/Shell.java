package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;
import java.util.Scanner;

public class Shell {
	
	 public static void main(String[] args){
		 StateShell st = new StateShell();
		 if (args.length > 0) {
			 batchMode(args, st);
		 } else {
			 interactiveMode(st);
		 }
	 }

	 private static void batchMode(String[] args, StateShell st){
		 try {
			 parseAndExecute(join(args), st);
		 } catch (IOException e) {
			 System.err.println(e.getMessage());
			 System.exit(1);
		 }
	 }
	 
	 private static String join(String[] args){
		 StringBuilder sb = new StringBuilder();
		 for (String s: args) {
			 sb.append(s);
			 sb.append(" ");
		 }
		 return sb.toString();
	 }
	 
	 private static void parseAndExecute(String arg, StateShell st) throws IOException{
		 String[] com = arg.trim().split("\\s*;\\s*"); 
		 for (String s: com){
			 String[] args = s.split("\\s+");
			 int argsNumber = -1;
			 AbstractCommand c = st.commands.elementAt(0);
			 for (int i = 0; i < st.commands.size(); ++i) {
				 if (args[0].equals(st.commands.elementAt(i).getName())){
					 c = st.commands.elementAt(i);
					 argsNumber = c.getNumberOfArguments();
					 break;
				 }
			 }
			 if (argsNumber == -1){
				 throw new IOException(args[0] + ": No such command");
			 } else if (argsNumber > args.length - 1) {
				 throw new IOException(args[0]+ ": Too few arguments");
			 }
			 c.execute(args);
		 }
	 }

	 private static void interactiveMode(StateShell st) {
		 Scanner sc = new Scanner(System.in);
		 do {
			 try {
				 System.out.print("$ ");
				 String s = sc.nextLine();
				 parseAndExecute(s, st);
			 } catch (IOException e) {
				 System.err.println(e.getMessage());
			 }	 
		 } while (!Thread.interrupted());
		 sc.close();
	 }
 }