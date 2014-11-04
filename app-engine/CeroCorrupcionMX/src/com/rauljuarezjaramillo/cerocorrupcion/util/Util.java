package com.rauljuarezjaramillo.cerocorrupcion.util;

/**
 * 
 * @author desarrollo
 *
 */
public class Util {

	private Util() {
	}
	/**
	 * Convierte una cadea a un int, lidiando con excepciones
	 * @param o la cadena a convertir
	 * @return un entero, o el valor 0 si no se pudo convertir
	 */
	public static int intOCero(String o) {
	    try {
	      return Integer.parseInt(o);
	    } catch (Exception localException) {
	    }
	    return 0;
	  }
	
	/**
	 * Convierte un objeto a un int, lidiando con excepciones
	 * @param o el objeto a convertir
	 * @return un entero, o el valor 0 si no se pudo convertir
	 */
	public static int intOCero(Object o) {
	    try {
	      return intOCero(o.toString());
	    } catch (Exception localException) {
	    }
	    return 0;
	  }

	/**
	 * Convierte una cadena a un double, lidiando con excepciones
	 * @param o la cadena a convertir
	 * @return un double, o el valor 0 si no se pudo convertir
	 */
	  public static double doubleOCero(String o) {
	    try {
	      return Double.parseDouble(o);
	    } catch (Exception localException) {
	    }
	    return 0.0D;
	  }
}
