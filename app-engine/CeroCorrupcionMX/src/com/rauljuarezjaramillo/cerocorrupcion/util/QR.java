package com.rauljuarezjaramillo.cerocorrupcion.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * Clase de utileria para generar codigos QR
 * @author Appix Creative
 *
 */
public class QR {

	/**
	 * Genera un codigo QR. La implementacion actual obtiene el codigo utilizando Google Charts API
	 * @param contenido el contenido del codigo QR
	 * @return la imagen del QR en forma de arreglo de bytes
	 */
	public static byte[] generarQR(String contenido) {
		URLFetchService urlFetch = URLFetchServiceFactory.getURLFetchService();
		HTTPResponse response;
		try {
			response = urlFetch.fetch(new URL("http://chart.apis.google.com/chart?cht=qr&chs=350x350&chl=asde&chld=H%7C0"));
			byte[] bytes = response.getContent();

			return bytes;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
