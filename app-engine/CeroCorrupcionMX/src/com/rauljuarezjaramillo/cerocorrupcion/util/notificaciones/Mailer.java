package com.rauljuarezjaramillo.cerocorrupcion.util.notificaciones;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.mail.MailServiceFactory;

/**
 * Builder para enviar emails
 * @author Appix Creative
 *
 */
public class Mailer {

	/**
	 * Define un archivo adjunto a agregar al email
	 * @author Appix Creative
	 *
	 */
	public static class MailAttach {
		byte[] bytes;
		String nombreEnCorreo;
		String mimeType;
		/**
		 * Construye una nueva definicion para un archivo adjunto
		 * @param bytes el contenido del attachment
		 * @param mimeType el tipo de contenido
		 * @param nombreEnCorreo el nombre que tendra el archivo en el correo
		 */
		MailAttach(byte[] bytes, String mimeType,String nombreEnCorreo) {
			this.bytes=bytes;
			this.nombreEnCorreo = nombreEnCorreo;
			this.mimeType=mimeType;
		}
	}

	Map<String, Object> map = new HashMap<String,Object>();

	/**
	 * Crea una nueva instancia para envio de emails
	 * @return un nuevo Mailer
	 */
	public static Mailer newMail() {
		return new Mailer();
	}

	/**
	 * Agrega un correo de destino a la lista (campo TO)
	 * @param correoDestino la direccion de correo a la cual se enviara
	 * @return esta instancia
	 */
	public Mailer destino(String correoDestino) {
		List<String> destinos = (List<String>) this.map.get("destinos");
		if (destinos == null) {
			destinos = new ArrayList<String>();
			this.map.put("destinos", destinos);
		}
		destinos.add(correoDestino);
		return this;
	}

	/**
	 * Agregar un correo a la lista de destinatarios CC
	 * @param correoDestinoCC la direccion de correo
	 * @return esta instancia
	 */
	public Mailer cc(String correoDestinoCC) {
		List<String> cc = (List<String>) this.map.get("cc");
		if (cc == null) {
			cc = new ArrayList<String>();
			this.map.put("cc", cc);
		}
		cc.add(correoDestinoCC);
		return this;
	}

	/**
	 * Agrega un coreo a la lista de destinatarios BCC
	 * @param correoDestinoBCC la direccion de correo
	 * @return esta instancia
	 */
	public Mailer bcc(String correoDestinoBCC) {
		List<String> bcc = (List<String>) this.map.get("bcc");
		if (bcc == null) {
			bcc = new ArrayList<String>();
			this.map.put("bcc", bcc);
		}
		bcc.add(correoDestinoBCC);
		return this;
	}

	/**
	 * El asunto del correo
	 * @param asunto el texto del asunto
	 * @return esta instancia
	 */
	public Mailer asunto(String asunto) {
		this.map.put("asunto", asunto);
		return this;
	}

	/**
	 * El contenido del correo
	 * @param mensaje el texto/html del correo
	 * @return esta instancia
	 */
	public Mailer mensaje(String mensaje) {
		this.map.put("mensaje", mensaje);
		return this;
	}

	/**
	 * Los archivos que se van a agregar al correo como attachments
	 * @return
	 */
	public List<Mailer.MailAttach> getArchivos() {
		List<MailAttach> archivos = (List<MailAttach>) this.map.get("archivos");
		if (archivos == null) {
			archivos = new ArrayList<MailAttach>();
			this.map.put("archivos", archivos);
		}
		return archivos;
	}


    /**
     * Agrega un archivo para envio. El contenido del archivo se basara en el parametro byte[] enviado
     * @param archivoStream contenido que tendra el archivo adjunto
     * @param mimeType el mimetype de archivoStream
     * @param nombreEnCorreo el nombre que se le dara al archivo adjunto en el correo
     * @return esta instancia
     * @throws IOException  si ocurre un error al crear el archivo o al escribir el contenido
     */
    public Mailer archivo(byte[] archivoStream, String mimeType,String nombreEnCorreo) throws IOException {
        List<MailAttach> archivos = getArchivos();
        archivos.add(new MailAttach(archivoStream,mimeType, nombreEnCorreo != null
                ? nombreEnCorreo
                : "archivo"));
        return this;
    }

    /**
     * Envia de manera sincrona el correo
     * @return esta instancia
     * @throws AddressException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
	public Mailer enviar() throws AddressException, MessagingException, UnsupportedEncodingException,IOException {
		return enviar(false);
	}

	/**
	 * Envia el correo a los destinatarios, adjuntando los archivos que se hayan agregado
	 * @param esAsincrono true si el envio se debe hacer en el background, false en caso contrario
	 * @return esta instancia
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public Mailer enviar(boolean esAsincrono) throws AddressException, MessagingException, UnsupportedEncodingException,IOException {
		if (esAsincrono)
			new Thread(new Runnable() {
				public void run() {

					try {
						_enviar();
					} catch (AddressException ex) {
						Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
					} catch (MessagingException ex) {
						Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException ex) {
						Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
					}	
				}
			}).start();
		else {
			_enviar();
		}
		return this;
	}

/*	private void _enviar() throws AddressException, MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress("admin@cerocorrupcionmx.appspotmail.com", "Cero Corrupcion"));

		List<String> destinos = (List<String>) this.map.get("destinos");
		if (destinos != null) {
			for (String destino : destinos) {
				try {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino, (String) this.map.get("origen"), "ISO-8859-1"));
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		destinos = (List<String>) this.map.get("cc");
		if (destinos != null) {
			for (String destino : destinos) {
				try {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(destino, (String) this.map.get("origen"), "ISO-8859-1"));
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		destinos = (List<String>) this.map.get("bcc");
		if (destinos != null) {
			for (String destino : destinos) {
				try {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(destino, (String) this.map.get("origen"), "ISO-8859-1"));
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}

		Multipart multipart = new MimeMultipart();
		String texto = (String) this.map.get("mensaje");
		MimeBodyPart messageBodyPart = null;

		if (texto != null) {
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(texto, "text/html");
			multipart.addBodyPart(messageBodyPart);
		}

		List<MailAttach> archivos = (List<MailAttach>) this.map.get("archivos");
		if (archivos != null) {
			for (Mailer.MailAttach archivo : archivos) {
				DataSource source = new ByteArrayDataSource(archivo.bytes, archivo.mimeType);
				messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler(source));
				//messageBodyPart.setContent(archivo.bytes, archivo.mimeType);
				messageBodyPart.setFileName(archivo.nombreEnCorreo);
				multipart.addBodyPart(messageBodyPart);
			}
		}
		message.setContent(multipart);

		message.setSubject((String) this.map.get("asunto"));

		Transport.send(message);
	}*/
	/**
	 * Se encarga del envio del correo
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void _enviar() throws AddressException, MessagingException, UnsupportedEncodingException,IOException {
		Properties props = new Properties();


MailService service = MailServiceFactory.getMailService(); 
                                MailService.Message msg = new MailService.Message(); 

                        msg.setSender("admin@cerocorrupcionmx.appspotmail.com"); 
                        
		List<String> destinos = (List<String>) this.map.get("destinos");
		if(destinos!=null){
			msg.setTo(destinos);
		}
		destinos = (List<String>) this.map.get("cc");
		if(destinos!=null){
			msg.setCc(destinos);
		}
		destinos = (List<String>) this.map.get("bcc");
		if(destinos!=null){
			msg.setBcc(destinos);
		}
		
		msg.setSubject((String) this.map.get("asunto")); 
         msg.setTextBody((String) this.map.get("mensaje")); 

		List<MailAttach> archivos = (List<MailAttach>) this.map.get("archivos");
		List<Attachment> attachments=new ArrayList<MailService.Attachment>();
		if (archivos != null) {
			for (Mailer.MailAttach archivo : archivos) {
				Attachment temp = new Attachment(archivo.nombreEnCorreo, archivo.bytes);
				attachments.add(temp);
			}
		}
		msg.setAttachments(attachments);
		service.send(msg);

	}
}
