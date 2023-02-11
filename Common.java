public class Common {

  static public int s2i (String s) {
    //Convierte una Cadena a entero
    int i = 0;

    try {
      i = Integer.parseInt(s.trim());
    } catch (NumberFormatException nfe) {
      System.out.println("NumberFormatException: " + nfe.getMessage());
    } 
    return i;
  }

  static public double R1 () {
    /*genera un número aleatorio siguiendo una distribución específica. 
    Utiliza otras tres funciones "R2", "R3" y "R4" para verificar si el número generado cumple con ciertas condiciones. */
    //java.util.Random generator = new java.util.Random(System.currentTimeMillis());
    java.util.Random generator = new java.util.Random();
    double U = generator.nextDouble();
    while (U < 0 || U >= 1) {
      U = generator.nextDouble();
    }
    double V = generator.nextDouble();
    while (V < 0 || V >= 1) {
      V = generator.nextDouble();
    }
    double X =  Math.sqrt((8/Math.E)) * (V - 0.5)/U;
    if (!(R2(X,U))) { return -1; }
    if (!(R3(X,U))) { return -1; }
    if (!(R4(X,U))) { return -1; }
    return X;
  }

  static public boolean R2 (double X, double U) {
    /*verifica si el cuadrado del número generado es menor o igual que (5 - 4 * Math.exp(.25) * U), 
    donde U es un número aleatorio generado previamente. */
    if ((X * X) <= (5 - 4 * Math.exp(.25) * U)) {
      return true;
    } else {
      return false;
    }
  }

  static public boolean R3 (double X, double U) {
    /*verifica si el cuadrado del número generado es mayor o igual que (4 * Math.exp(-1.35) / U + 1.4), 
    donde U es un número aleatorio generado previamente */
    if ((X * X) >= (4 * Math.exp(-1.35) / U + 1.4)) {
      return false;
    } else {
      return true;
    }
  }

  static public boolean R4 (double X, double U) {
    /*verifica si el cuadrado del número generado es menor que (-4 * Math.log(U)), 
    donde U es un número aleatorio generado previamente. */
    if ((X * X) < (-4 * Math.log(U))) {
      return true;
    } else {
      return false;
    }
  }

}

