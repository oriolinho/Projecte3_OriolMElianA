package servidor;

import java.io.DataInputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;


public class ServeiChat implements Runnable {

	SSLSocket socket;
	DataInputStream in;
	String nom;
	Usuaris usuari = new Usuaris();

	public ServeiChat(Usuaris usuari, SSLSocket socket) throws Exception {
		this.usuari = usuari;
		this.socket = socket;

		in = new DataInputStream(socket.getInputStream());

		this.nom = in.readUTF(); 
		//System.out.println(nom);
		usuari.AddClient(nom, socket); //afegeix els usuaris per visualitzar-ho a la pantalla...
	}

	public void run() {
		try {
			while (true) {
				String msg = in.readUTF();
				usuari.enviarMissatge(msg, nom);
			}
		} catch (Exception e) {
			usuari.desconectarClient(this.nom);
		}
	}
}
