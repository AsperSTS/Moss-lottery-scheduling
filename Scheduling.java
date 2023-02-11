// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.
// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.*;

public class Scheduling {

  private static int processnum = 10;
  private static int meanDev = 1000;
  private static int standardDev = 100;
  private static int runtime = 1000;
  private static Vector processVector = new Vector();
  private static Results result = new Results("null","null",0,0);
  private static String resultsFile = "Summary-Results";

  private static void Init(String file) {
    File f = new File(file);
    String line;
    String tmp;
    int cputime = 0;
    int ioblocking = 0;
    double X = 0.0;
    int tickets = 0;
    

    try {   
      //BufferedReader in = new BufferedReader(new FileReader(f));
      DataInputStream in = new DataInputStream(new FileInputStream(f));
      while ((line = in.readLine()) != null) {
        if (line.startsWith("numprocess")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          processnum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("meandev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          meanDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("standdev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          standardDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("process")) {

          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          ioblocking = Common.s2i(st.nextToken());
          tickets = Common.s2i(st.nextToken());
          
          X = Common.R1(); 
          while (X == -1.0) {
            X = Common.R1();
          }
          X = X * standardDev;
          cputime = (int) X + meanDev;
          System.out.println("Tickets: "+tickets);
          System.out.println("ioBlocking: "+ioblocking);
          System.out.println("CpuTime: "+cputime+"\n");

          processVector.addElement(new sProcess(cputime, ioblocking, 0, 0, 0,tickets));
        }

        if (line.startsWith("runtime")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          runtime = Common.s2i(st.nextToken());
        }
      }
      in.close();
    } catch (IOException e) { /* Handle exceptions */ }
  }

  private static void debug() {
    int i = 0;

    System.out.println("processnum " + processnum);
    System.out.println("meandevm " + meanDev);
    System.out.println("standdev " + standardDev);
    int size = processVector.size();
    for (i = 0; i < size; i++) {
      sProcess process = (sProcess) processVector.elementAt(i);
      System.out.println("process " + i + " " + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.numblocked);
    }
    System.out.println("runtime " + runtime);
  }

  public static void main(String[] args) {

    int i = 0;
    int totalTickets = 0;
    
    //Mensajes de errores 
    if (args.length != 1) {
      System.out.println("Usage: 'java Scheduling <INIT FILE>'");
      System.exit(-1);
    }
    File f = new File(args[0]);
    if (!(f.exists())) {
      System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
      System.exit(-1);
    }  
    if (!(f.canRead())) {
      System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
      System.exit(-1);
    }
    System.out.println("Working...");
    //Mensaje de se esta trabajando
    Init(args[0]);

    //System.out.println("Vector size: "+processVector.size());
    //System.out.println("Process num: "+processnum);
    if (processVector.size() < processnum) {
      //Si el tamano del vector es menor que el numero de procesos, agregamos otro proceso
      i = 0;
      while (processVector.size() < processnum) {       
          double X = Common.R1();
          while (X == -1.0) {
            X = Common.R1();
          }
          X = X * standardDev;
        int cputime = (int) X + meanDev;
        processVector.addElement(new sProcess(cputime,i*100,0,0,0,0));  
        i++;
      }
    }

    //Contabilizamos todos los tickets
    sProcess tmp = (sProcess) processVector.elementAt(0);
    for(int k=0; i<processVector.size();i++){
      tmp = (sProcess) processVector.elementAt(i);
      totalTickets+=tmp.tickets;
    }
    

    result = SchedulingAlgorithm.Run(runtime, processVector, result,totalTickets);    
    //Instanciamos el Algoritmo

    try {
      //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      out.println("Scheduling Type: " + result.schedulingType);
      out.println("Scheduling Name: " + result.schedulingName);
      out.println("Simulation Run Time: " + result.compuTime);
      out.println("Mean: " + meanDev);
      out.println("Standard Deviation: " + standardDev);
      out.println("Total tickets: " + totalTickets);
      out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked\tTickets");


      for (i = 0; i < processVector.size(); i++) {
        sProcess process = (sProcess) processVector.elementAt(i);
        out.print(Integer.toString(i));
        if (i < 100) { 
          out.print("\t\t"); 
        } else { 
          out.print("\t"); 
        } 
        
        out.print(Integer.toString(process.cputime));

        if (process.cputime < 100) { 
          out.print(" (ms)\t\t");   
        } else { 
          out.print(" (ms)\t"); 
        }

        out.print(Integer.toString(process.ioblocking));

        if (process.ioblocking < 100) { 
          out.print(" (ms)\t\t"); 
        } else { 
          out.print(" (ms)\t"); 
        } 
        
        out.print(Integer.toString(process.cpudone-1));

        if (process.cpudone < 100) { 
          out.print(" (ms)\t\t");
        } else { 
          out.print(" (ms)\t"); 
        }

        out.print(Integer.toString(process.numblocked) + " times"+"\t");
        if (process.numblocked < 10) { 
          out.print("\t");
        } else { 
          out.print(""); 
        }
        out.println(process.tickets);
      }
      out.close();
    } catch (IOException e) { 
      /* Handle exceptions */ 
      }

      System.out.println("Completed.");
  }
}

