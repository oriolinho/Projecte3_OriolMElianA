 package servidor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import BensPr.Escriure;
import BensPr.Llegir;
import BensPr.Sobreescriure;
import javafx.application.Platform;

public class Usuaris {
	HashMap<String, DataOutputStream> clientmap = new HashMap<String, DataOutputStream>();
	String str; 
	static SecretKey key = keygenKeyGeneration(128);
	
	public synchronized void AddClient(String name, Socket socket) {
		try {
			clientmap.put(name, new DataOutputStream(socket.getOutputStream()));
			enviarMissatge(name + " s'ha connectat.", "Server");
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public synchronized void desconectarClient(String name) {
		try {
			clientmap.remove(name);
			enviarMissatge(name + " desconnectat.", "Server");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public synchronized void enviarMissatge(String msg, String name) throws Exception {
		str = name + " : " + msg;		
		System.out.println(str);

		Platform.runLater(() -> { 
			ChatServer.logs.setText(ChatServer.logs.getText() + "\n" + str);
		});
		System.out.println(ChatServer.logs.getText());
		
		File carpeta;
		//Creem la carpeta on guardarem el document.
		carpeta = new File (File.listRoots()[0]+"DocumentsXat");
		carpeta.mkdir();
		//Escribim dins del document la informació obtuguda del str
		Escriure fitxerXat = new Escriure(carpeta.getAbsolutePath() + "\\msgxat.txt");
		fitxerXat.escriu(str);
		
	
		Iterator iterator = clientmap.keySet().iterator();
		while (iterator.hasNext()) {
			String clientname = (String) iterator.next();
			clientmap.get(clientname).writeUTF(name + " : " + msg);
			
		}
	}
	
	
	
	
	public static void guardarfile(String msg, String name) throws IOException{
		//Pasem la ruta de la carpeta creada anteriorment		
		String rutacarpeta = "C:\\DocumentsXat";
		
		//Generem la contrasenya per encriptar el document		
		String clauAES = Base64.getEncoder().encodeToString(key.getEncoded());
		
		//guardem la contrasenya dins del docuement		
		Escriure clauFitxer = new Escriure(rutacarpeta + "\\clau.txt");
		clauFitxer.escriu(clauAES);
		
		//Obtenim la ruta del document que te la informació del xat	
		String ruta = "C:\\DocumentsXat\\msgxat.txt";
		
		//llegim el docuement
		Llegir llegirfitxe = new Llegir(ruta);
		
		//Encriptar el fitxer amb la clau creada//		
		byte[] textXifrat = encryptData(key,llegirfitxe.llegir().getBytes());
		///Sobre escriure fitxer encriptat		
		Sobreescriure fitxerXifratEscrit = new Sobreescriure(ruta);
		fitxerXifratEscrit.escriu(Base64.getEncoder().encodeToString(textXifrat));
		
		
		
		
	}
	
	public static void desencriptarfitxer() throws IOException{
		//Obtenim la ruta del document que tenim encriptat
		String rutacarpeta = "C:\\DocumentsXat\\msgxat.txt";
		
		//Llegim el fitxer on tenim la clau
		   Llegir keyLlegida = new Llegir(rutacarpeta + "\\clau.txt");
		   
		   //Ibtenim la ruta del document que s'ha de desencriptar	    
		   String ruta2 = "C:\\DocumentsXat\\msgxat.txt";
 		  
		   //Llegim el fitxer
		   Llegir fitxerDesxifrar = new Llegir(ruta2);
		   String llTextXifrat = fitxerDesxifrar.llegir();
		   
		   byte[] contingutXifrat = Base64.getDecoder().decode(llTextXifrat);
		   
		   //Desencriptem la informació que tenim dins del document
		   byte[] textDesxifrat = decryptData(key,contingutXifrat);
		   
		   FileOutputStream fileOuputStream = new FileOutputStream(ruta2);
		   fileOuputStream.write(textDesxifrat);
		   //Tanquem el document
		   fileOuputStream.close();
		   
		   System.out.println("El text esta desencriptat.");
	}
	
	
	
	//Desxifrar RSA directe

	public static byte[] encryptData(SecretKey sKey, byte[] data) {

		byte[] encryptedData = null;

		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sKey);
			encryptedData = cipher.doFinal(data);
		} catch (Exception ex) {
			System.err.println("Error xifrant les dades: " + ex);
		}
		return encryptedData;
	}

	//Desxifrar frases/paraules/dades
	public static byte[] decryptData(SecretKey sKey, byte[] data) {

		byte[] decryptedData = null;

		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, sKey);
			decryptedData = cipher.doFinal(data);
		} catch (Exception ex) {
			System.err.println("Error xifrant les dades: " + ex);
		}
		return decryptedData;
	}



	//GENERAR CLAU ALEATORIA
	public static SecretKey keygenKeyGeneration(int keySize) {

		SecretKey sKey = null;

		if ((keySize == 128)||(keySize == 192)||(keySize == 256)) {
			try {
				KeyGenerator kgen = KeyGenerator.getInstance("AES");
				kgen.init(keySize);
				sKey = kgen.generateKey();

			} catch (NoSuchAlgorithmException ex) {
				System.err.println("Generador no disponible.");
			}
		}
		return sKey;
	}
}
