import java.util.Vector;
import java.io.*;
import java.util.Random;

public class SchedulingAlgorithm {

  public static int getRandomNumberInRange(int min, int maxTickets) {

		if (min >=maxTickets) {
			throw new IllegalArgumentException("El maximo debe ser mas grande que el minimo");
		}

		Random r = new Random();
		return r.nextInt((maxTickets - min) + 1) + min;
	}

  public static Results Run(int runtime, Vector processVector, Results result, int  totalTickets) {
    int i = 0;
    int comptime = 0;
    int currentProcess = 0;
    int previousProcess = 0;
    int size = processVector.size();
    int completed = 0;
    int n = 0;
    int ticketGanador;
    int sumatoria=0;
    String resultsFile = "Summary-Processes";

    result.schedulingType = "No apropiativo";
    result.schedulingName = "Algoritmo de loteria"; 
    try {
      //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
      //OutputStream out = new FileOutputStream(resultsFile);
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      sProcess process = (sProcess) processVector.elementAt(currentProcess);
      //out.println("Process: " + currentProcess + " registered... (cputime: " + process.cputime + " ,ioblocking:  " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
      out.println("Process: " + currentProcess + " registered...  (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
      
      //Mientras el tiempo de computo es menor que el tiempo de ejecucion final
      while (comptime < runtime) {

        //System.out.println("cpudone: "+ process.cpudone+" , cputime: "+process.cputime);
        if (process.cpudone == process.cputime) {
          completed++;
          out.println("Process: " + currentProcess + " completed...   (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
          
          if (completed == size) {
            result.compuTime = comptime;  //No modificar
            out.close();
            return result;
          }
          //for (i = 0; i < size; i++) {
            //System.out.println("size: "+size);
          sumatoria = 0;
          ticketGanador = getRandomNumberInRange(1,totalTickets);

          for (i = size - 1; i >= 0; i--) {
            //System.out.println("size: "+size);
            process = (sProcess) processVector.elementAt(i);
            sumatoria+=process.tickets;
            //System.out.println("Sumatoria: "+sumatoria+" , ganador: "+ticketGanador);
            if(sumatoria>=ticketGanador){
              //System.out.println("Entra ciclo 1");
              if (process.cpudone < process.cputime) { 
                currentProcess = i;
              }
            }
            
          }

          process = (sProcess) processVector.elementAt(currentProcess);
          out.println("Process: " + currentProcess + " registered...  (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
        }      

        //System.out.println("ioblocking: "+ process.ioblocking+" , ionext: "+process.ionext);

        if (process.ioblocking == process.ionext) {
          out.println("Process: " + currentProcess + " I/O blocked... (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
          process.numblocked++;
          process.ionext = 0; 
          previousProcess = currentProcess;
          //for (i = 0; i < size; i++) {

          
            sumatoria = 0;
            ticketGanador = getRandomNumberInRange(1,totalTickets);
  
            for (i = size - 1; i >= 0; i--) {
              //System.out.println("size: "+size);
              process = (sProcess) processVector.elementAt(i);
              sumatoria+=process.tickets;
              //System.out.println("Sumatoria: "+sumatoria+" , ganador: "+ticketGanador);
              if(sumatoria>=ticketGanador){
                //System.out.println("Entra ciclo 2");
                if (process.cpudone < process.cputime && previousProcess != i) { 
                  currentProcess = i;
                }
              }
              
            }
          process = (sProcess) processVector.elementAt(currentProcess);
          out.println("Process: " + currentProcess + " registered...  (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
        }        
        process.cpudone++;       
        if (process.ioblocking > 0) {
          
          process.ionext++;
          //System.out.println("ioblocking: "+process.ioblocking+" , ionext: "+ process.ionext);
        }
        comptime++;
        n++;
      }
      out.close();
    } catch (IOException e) { 
      
    }
    result.compuTime = comptime;
    return result;
  }
}

/*

import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector processVector, Results result) {
    int i = 0;
    int comptime = 0;
    int size = processVector.size();
    int completed = 0;
    int ticketCount[] = new int[size];
    int totalTickets = 0;
    int winner = 0;
    String resultsFile = "Summary-Processes";

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "Lottery Scheduling"; 
    try {
      //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
      //OutputStream out = new FileOutputStream(resultsFile);
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      
      for (i = 0; i < size; i++) {
        sProcess process = (sProcess) processVector.elementAt(i);
        ticketCount[i] = process.priority;
        totalTickets += process.priority;
      }
      
      while (comptime < runtime) {
        int ticket = (int) (Math.random() * totalTickets);
        for (i = 0; i < size; i++) {
          if (ticket < ticketCount[i]) {
            winner = i;
            break;
          }
          ticket -= ticketCount[i];
        }
        
        sProcess process = (sProcess) processVector.elementAt(winner);
        
        if (process.cpudone == process.cputime) {
          completed++;
          out.println("Process: " + winner + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
          if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }
        }      
        if (process.ioblocking == process.ionext) {
          out.println("Process: " + winner + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
          process.numblocked++;
          process.ionext = 0; 
        }        
        process.cpudone++;       
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
      }
      out.close();
    } catch (IOException e) { }
    result.compuTime = comptime;
    return result;
  }
}
*/ 



// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.
/* 
import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector processVector, Results result) {
    int i = 0;
    int comptime = 0;
    int currentProcess = 0;
    int previousProcess = 0;
    int size = processVector.size();
    int completed = 0;
    String resultsFile = "Summary-Processes";

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "First-Come First-Served"; 
    try {
      //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
      //OutputStream out = new FileOutputStream(resultsFile);
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      sProcess process = (sProcess) processVector.elementAt(currentProcess);
      out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
      while (comptime < runtime) {
        if (process.cpudone == process.cputime) {
          completed++;
          out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
          if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }
          for (i = size - 1; i >= 0; i--) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime) { 
              currentProcess = i;
            }
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
        }      
        if (process.ioblocking == process.ionext) {
          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
          process.numblocked++;
          process.ionext = 0; 
          previousProcess = currentProcess;
          for (i = size - 1; i >= 0; i--) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime && previousProcess != i) { 
              currentProcess = i;
            }
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
        }        
        process.cpudone++;       
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
      }*/ /*
      out.close();
    } catch (IOException e) {  }
    result.compuTime = comptime;
    return result;
  }
} */
