package com.rauljuarezjaramillo.cerocorrupcion.util.notificaciones;

import java.util.Map;
import java.util.Set;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message.Builder;

public class Push {

	public Push() {

	}
	
	public static void enviarNotificacion(Map<String,String> mapa, String id) {
		Set<Map.Entry<String,String>> entradas=mapa.entrySet();
		Builder message = new Message.Builder();
		for(Map.Entry<String, String> entrada : entradas){
			message.addData(entrada.getKey(), entrada.getValue());
		}
		enviarNotificacion(message.build(), id);
	}

	public static void enviarNotificacion(Message mensaje, String id) {
		Sender sender = new Sender("AIzaSyCHs6xT-8Yik5DUxT79cWkLilAnsuDf_Aw");

		try {
			Result result = sender.send(mensaje, id, 5);
			System.out.println("Notificacion Result: "+result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
