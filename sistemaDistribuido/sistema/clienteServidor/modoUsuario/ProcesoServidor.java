/*
	Alumna:  Guerrero Muñoz Silvia Veronica Guadalupe
	Sección: 03
	practica 5
*/


package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;

/**
 * 
 */
public class ProcesoServidor extends Proceso{

	/**
	 * 
	 */
	public ProcesoServidor(Escribano esc){
		super(esc);
		start();
	}

	/**
	 * 
	 */
	public void run(){
		imprimeln("Proceso servidor en ejecucion.");
		byte[] solServidor=new byte[1024];
		byte[] respServidor;
		String mensajeservidor = null;
		while(continuar()){
			Nucleo.receive(dameID(),solServidor);
			short codop = (short) ((solServidor[9] << 8) + (solServidor[8]&0x00FF));
			imprimeln("\nCodigo de Operacion: "+codop);
			switch(codop){
			case 1: imprimeln("\nSe ha solicitado la creacion de archivos..");
			mensajeservidor = "Se ha procedido a la creacion del archivo";
			break;
			case 2: imprimeln("\nSe ha solicitado la eliminacion de archivos..");
			mensajeservidor = "Se ha procedido a la eliminacion del archivo";
			break;
			case 3: imprimeln("\nSe ha solicitado la lectura de archivos..");
			mensajeservidor = "Se ha procedido a la lectura del archivo";
			break;
			case 4: imprimeln("\nSe ha solicitado la escritura de archivos..");
			mensajeservidor = "Se ha procedido a la escritura del archivo";
			break;
			}
			imprimeln("\nEl cliente envió el mensaje: ");
			char temporal;
			for(int j=9; j<1016; j++){
			temporal = (char) solServidor[j];
			imprimeln(String.valueOf(temporal));
			}
			respServidor=new byte[1024];
			Pausador.pausa(1000);  //sin esta línea es posible que Servidor solicite send antes que Cliente solicite receive
			imprimeln("\nEnviando Respuesta a cliente...");
			int guardaid = dameID();
			respServidor[0]=(byte)(guardaid);
			respServidor[1]=(byte)(guardaid>>8);
			respServidor[2]=(byte)(guardaid>>16);
			respServidor[3]=(byte)(guardaid>>24);   
			respServidor[4]=solServidor[0];
			respServidor[5]=solServidor[1];
			respServidor[6]=solServidor[2];
			respServidor[7]=solServidor[3];
			byte[] byteacadena = mensajeservidor.getBytes();
			int contador = 8;
			for(int j=0; j<mensajeservidor.length(); j++){
			respServidor[contador] = byteacadena[j];	
			contador++;
			}
			int destino =  (int) (( (solServidor[3]&0x000000FF) << 24) + ((solServidor[2]&0x000000FF) << 16) 
					+ ((solServidor[1]&0x000000FF) << 8) + (solServidor[0]&0x000000FF));
			Nucleo.send(destino,respServidor);
		}
	}
}
