package com.rauljuarezjaramillo.cerocorrupcion.seguridad;

/**
 *
 * @author Appix Creative
 */
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.security.Key;

import javax.crypto.Cipher;

import com.google.common.io.BaseEncoding;

public class Encrypter {

	private static String algorithm = "DESede";
	private static Key key = null;
	private static Cipher cipherEnc = null;
	private static Cipher cipherDec = null;

	/**
	 * Inicializa el encrypter con la llave que se encuentra en la ruta keyPath
	 * @param keyPath la ruta al archivo donde se tiene guardada la llave privada
	 * @throws Exception si no se encuentra el archivo o no representa una llave valida para esta aplicacion
	 */
	public void setUp(String keyPath) throws Exception {

		FileInputStream fis = new FileInputStream(keyPath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		key = (Key) ois.readObject();
		fis.close();
		ois.close();

		cipherEnc = Cipher.getInstance(algorithm);
		cipherEnc.init(Cipher.ENCRYPT_MODE, key);
		cipherDec = Cipher.getInstance(algorithm);
		cipherDec.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * Encripta un identificador
	 * @param id el identificador a encriptar
	 * @return representancion en cadena base 32 del resultado de encriptar el identiifcador
	 * @throws Exception si no se puede encriptar, o el encripter no ha sido inicializado
	 */
	public String encrypt(Identificador id) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(id.id);
		byte[] bytes = buffer.array();

		String encrypted = null;
		synchronized (cipherEnc) {
			encrypted = new String(BaseEncoding.base32().encode(cipherEnc.doFinal(bytes))).replace('_', 'ñ').replace("-", "Ñ")
					.replace("=", "");
		}
		System.out.println("encrypted = " + encrypted);
		return encrypted;

	}

	/**
	 * Desencripta una cadena previamente encriptada por este Encrypter
	 * @param encrypted la cadena base 32 encriptada mediante el metodo encript
	 * @return el identificador que se encripto
	 * @throws Exception si no se puede desencriptar la cadena o el encrypter no ha sido inicializado
	 */
	public  Identificador decryptIdentificador(String encrypted) throws Exception {
		encrypted = encrypted.replace('ñ', '_').replace('Ñ', '-');
		for (int i = 0; i < 3; i++) {
			String otherEncrypted = encrypted;
			for (int j = 0; j < i; j++) {
				otherEncrypted = otherEncrypted.concat("=");
			}
			System.out.println("encrypted = " + otherEncrypted);
			try {
				
				byte[] bytes = BaseEncoding.base32().decode(otherEncrypted);
				synchronized (cipherDec) {
					bytes = cipherDec.doFinal(bytes);
				}
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				Identificador identificador = new Identificador(buffer.getInt());
				return identificador;
			} catch (Exception ex) {
				// System.out.println("i = " + i);
				// System.out.println("ex = " + ex);
			}

		}
		throw new Exception("No se puede desencriptar");
	}

	/**
	 * Representa un identificador que puede encriptarse por Encrypter
	 * @author Appix Creative
	 *
	 */
	public static class Identificador {

		public final int id;

		public Identificador(int id) {
			this.id = id;
		}
	}
}
