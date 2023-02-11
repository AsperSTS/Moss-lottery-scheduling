import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector processVector, Results result, int ticketsT) {
    int i = 0;
    int comptime = 0;
    int size = processVector.size();
    int completed = 0;
    int ticketCount = 0;
    int totalTickets = 0;
    int winner = 0;
    int ticketVector[] = new int[size];
    String resultsFile = "Summary-Processes";
    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "Lottery Scheduling"; 

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      System.out.println("size "+size);
      for (i = 0; i < size; i++) {
        sProcess process = (sProcess) processVector.elementAt(i);
        totalTickets += process.tickets;
        ticketVector[i] = process.tickets;
      }
      System.out.println("Total tickets: "+totalTickets);
      
      while (comptime <= runtime) {
        ticketCount = 0;
        for(i = 0; i < size; i++) { 
          if (ticketVector[i] == 0){
            ticketVector[i] = (int) (Math.random() * 10); // asignar ticketsT si el proceso se queda sin tickets
            totalTickets += ticketVector[i];
          } 
        }
        
        int ticket = (int) (Math.random() * totalTickets);
        
        for(i = 0; i < size; i++) {  
          ticketCount += ticketVector[i];   
          sProcess aux = (sProcess) processVector.elementAt(i);  
          if (ticketCount>=ticket ) {
            if(aux.cpudone <= aux.cputime){
              winner = i;     
              ticketVector[winner]--;    
              break;
            }
          }
        }
        sProcess process = (sProcess) processVector.elementAt(winner);

        if (process.cpudone == process.cputime) {
          completed++;
          out.println("\nProcess: " + winner + " completed...   (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )\n");
          if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }
        }      
        if (process.ioblocking == process.ionext) {
          out.println("Process: " + winner + " Registered...  (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
          out.println("Process: " + winner + " I/O blocked... (cputime: " + process.cputime + " , ioblock:  " + process.ioblocking + " , cpudone: " + process.cpudone+" )");
          process.numblocked++;
          process.ionext = 0; 
        }        
        if (process.cpudone <= process.cputime) {
          process.cpudone++;      
        }   
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
      }
      out.close();
    } catch (IOException e) {  

    }
    result.compuTime = comptime;
    return result;
  }
}
