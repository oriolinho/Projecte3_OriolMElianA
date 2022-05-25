package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import client.Client;
import javafx.application.Platform;


public class IniciarChat implements Runnable {
	SSLSocket socket = null;
	//Socket socket = null; 
	Usuaris usuari = new Usuaris(); 
	//ServerSocket servidor_socket = null;
	SSLServerSocket servidor_socket = null; 

	int contadorClients = 0;
	Thread thread[] = new Thread[10];//Accepta un màxim de 10 clients
	

	@Override
	public void run() {
		try {
			//fiquem el certificat ssl del servidor
			System.setProperty("javax.net.ssl.keyStore", "serverKey.jks");
			System.setProperty("javax.net.ssl.keyStorePassword","servpass");
			SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			//servidor_socket = (SSLServerSocket) new ServerSocket(8080);
			// es crea el socket amb SSL
			servidor_socket = (SSLServerSocket) serverFactory.createServerSocket(8080);

			while (true) {
				socket = (SSLSocket) servidor_socket.accept(); 

				Date d1 = new Date();
				SimpleDateFormat data = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
				String dataFormat = data.format(d1);
				
				Platform.runLater(() -> {
					ChatServer.logs.setText(ChatServer.logs.getText() + "\nNou client connectat. " 
							+ dataFormat); //a la pantalla del servidor surtirar que s'ha conectat un client i data i hora d'aquest
				});

				thread[contadorClients] = new Thread(new ServeiChat(usuari, socket)); //es crida el fil per generar el client , cridant la classe serveichat
				thread[contadorClients].start();
				contadorClients++; //suma del client
				
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
