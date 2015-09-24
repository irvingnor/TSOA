/*
	Alumna:  Guerrero Mu�oz Silvia Veronica Guadalupe
	Secci�n: 03
	practica 5
*/


package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.NameServer;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;

/**
 * 
 */
public final class MicroNucleo extends MicroNucleoBase{
	Hashtable<Integer, Emision> tablaEmision  = new Hashtable<Integer, Emision>();
	Hashtable<Integer, byte[]> tablaRecepcion  = new Hashtable<Integer, byte[]>();
	
	private Hashtable<Integer, Queue<byte[]>> buzones;
	private Hashtable<Integer, byte[]> tablaTA;
	private int siguienteTA;
	
	private static MicroNucleo nucleo=new MicroNucleo();
	
	/**
	 * 
	 */
	private MicroNucleo(){
		buzones = new Hashtable<Integer, Queue<byte[]>>();
		tablaTA = new Hashtable<Integer, byte[]>();
		siguienteTA = 1;
	}

	
	public final void insertaTablaEmision(ParMaquinaProceso asa)
	{
		Emision e1 = new Emision();
		e1.setIp(asa.dameIP());
		e1.setNombre(asa.damenombre());
		e1.setId(asa.dameID());
		
		tablaEmision.put(asa.dameID(),e1);
	}
	
	
	public final void dameBuzon(int idProcesoServidor)
	{
		Queue<byte []> buzon = new LinkedList<byte[]>();
		buzones.put(new Integer(idProcesoServidor), buzon);
	}
	/**
	 * 
	 */
	public final static MicroNucleo obtenerMicroNucleo(){
		return nucleo;
	}

	/*---Metodos para probar el paso de mensajes entre los procesos cliente y servidor en ausencia de datagramas.
    Esta es una forma incorrecta de programacion "por uso de variables globales" (en este caso atributos de clase)
    ya que, para empezar, no se usan ambos parametros en los metodos y fallaria si dos procesos invocaran
    simultaneamente a receiveFalso() al reescriir el atributo mensaje---*/
	byte[] mensaje;

	public void sendFalso(int dest,byte[] message){
		System.arraycopy(message,0,mensaje,0,message.length);
		notificarHilos();  //Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
	}

	public void receiveFalso(int addr,byte[] message){
		mensaje=message;
		suspenderProceso();
	}
	/*---------------------------------------------------------*/

	/**
	 * 
	 */
	protected boolean iniciarModulos(){
		return true;
	}

	/**
	 * 
	 */
	protected void sendVerdadero(int dest,byte[] buffer){
		String ip;
		int iddest;
		
	//	sendFalso(dest,message);
		imprimeln("El proceso invocante es el "+super.dameIdProceso());	
		//lo siguiente aplica para la pr�ctica #2
		ParMaquinaProceso pmp=dameDestinatarioDesdeInterfaz();
		try{
		Emision em = tablaEmision.get(dest);
		ip =  em.getIp();
		iddest = em.getId();
		} catch (NullPointerException e) {
		ip =  ProcesoCliente.ipserver;
		iddest = ProcesoCliente.idserver;
		}
		int idorigen = super.dameIdProceso();
		imprimeln("Enviando mensaje a IP="+ip+" ID="+iddest);
		buffer[0]=(byte)(idorigen);
		buffer[1]=(byte)(idorigen>>8);
		buffer[2]=(byte)(idorigen>>16);
		buffer[3]=(byte)(idorigen>>24);  
		buffer[4]=(byte)(iddest);
		buffer[5]=(byte)(iddest>>8);
		buffer[6]=(byte)(iddest>>16);
		buffer[7]=(byte)(iddest>>24); 
		DatagramSocket socketEmision;
		DatagramPacket dp;

			try{
			socketEmision=new DatagramSocket(); 
			dp=new DatagramPacket(buffer,buffer.length,InetAddress.getByName(ip),1911);
			socketEmision.send(dp);
			imprimeln("Mensaje Enviado Exitosamente!!");
		}catch(SocketException e){
		}catch(UnknownHostException e){
		}catch(IOException e){
		}

	}


	protected void receiveVerdadero(int addr,byte[] message){
	//	receiveFalso(addr,message);
		//el siguiente aplica para la pr�ctica #2
		//tablaRecepcion.put(addr, message);
		//suspenderProceso();
		Queue<byte[]> buzonTmp = null;
		
		try {
			buzonTmp = (Queue<byte[]>) buzones.get(addr);
		} catch (Exception e) {
			
		}
		
		if(buzonTmp == null)//Es un cliente
		{
			println("Cliente entra a receiveVerdadero");
			tablaRecepcion.put(new Integer(addr), message);
			suspenderProceso();
		}
		else
		{
			if(buzonTmp.size() == 0)
			{
				tablaRecepcion.put(new Integer(addr), message);
				println("Servidor no tiene mensajes en buzon");
				suspenderProceso();
			}
			else
			{
				println("Servidor tiene mensaje en buzon");
				byte[] messageB = (byte[])buzonTmp.poll();
				System.arraycopy(messageB, 0, message, 0, messageB.length);
			}
		}
		
	}

	/**
	 * Para el(la) encargad@ de direccionamiento por servidor de nombres en pr�ctica 5  
	 */

	protected void sendVerdadero(String dest,byte[] message){
		/*String ip;
		String nombre;
		imprimeln("El proceso invocante es el "+super.dameIdProceso());
		ParMaquinaProceso pmp=dameDestinatarioDesdeInterfaz();
		Emision em = tablaEmision.get(dest);
		ip =  pmp.dameIP();
		nombre = pmp.damenombre();
				
		int idorigen = super.dameIdProceso();
		imprimeln("Enviando mensaje a IP="+ip+" ID="+nombre);
		buffer[0]=(byte)(idorigen);
		buffer[1]=(byte)(idorigen>>8);
		buffer[2]=(byte)(idorigen>>16);
		buffer[3]=(byte)(idorigen>>24);  
		buffer[4]=(byte)(iddest);
		buffer[5]=(byte)(iddest>>8);
		buffer[6]=(byte)(iddest>>16);
		buffer[7]=(byte)(iddest>>24);
		DatagramSocket socketEmision;
		DatagramPacket dp;

			try{
			socketEmision=new DatagramSocket(); 
			dp=new DatagramPacket(buffer,buffer.length,InetAddress.getByName(ip),1911);
			socketEmision.send(dp);
			imprimeln("Mensaje Enviado Exitosamente!!");
		}catch(SocketException e){
		}catch(UnknownHostException e){
		}catch(IOException e){
		}
*/
	}
	

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en pr�ctica 5
	 */
	
	protected void sendNBVerdadero(int dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en pr�ctica 5
	 */
	protected void receiveNBVerdadero(int addr,byte[] message){
	}

	/**
	 * 
	 */
	@SuppressWarnings({ "resource", "unused" })
	public void run(){
		DatagramSocket socketRecepcion;
		int puertoEntrada=1911;
		while(seguirEsperandoDatagramas()){
			/* Lo siguiente es reemplazable en la pr�ctica #2,
			 * sin esto, en pr�ctica #1, seg�n el JRE, puede incrementar el uso de CPU
			 */ 
			try {
				socketRecepcion=new DatagramSocket(puertoEntrada);
				byte[] buffer=new byte[1024];
				DatagramPacket dp;
				dp=new DatagramPacket(buffer,buffer.length);
				while(true){
					try {
						socketRecepcion.receive(dp);
						String ip = dp.getAddress().getHostAddress();
						imprimeln("IP emisora: "+ip);
						imprimeln(new String(buffer,0,dp.getLength()));
						
						int origen =  (int) (( (buffer[3]&0x000000FF) << 24) + ((buffer[2]&0x000000FF) << 16) 
								+ ((buffer[1]&0x000000FF) << 8) + (buffer[0]&0x000000FF));
						int destino =  (int) (( (buffer[7]&0x000000FF) << 24) + ((buffer[6]&0x000000FF) << 16) 
								+ ((buffer[5]&0x000000FF) << 8) + (buffer[4]&0x000000FF));
						
						
						if(buffer[8] == (byte)01 && buffer[9] == (byte)00)//TA
						{
							super.reanudarProceso(super.dameProcesoLocal(destino));
						}
						
						
						if(buffer[8] == (byte)01 && buffer[9] == (byte)01)//AU
						{
							try {
								sleep(5000);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						
						 Proceso p = dameProcesoLocal(destino);
						 try{
							 
							 if(super.dameProcesoLocal(destino) != null)
								{
									byte[] tmpMessage = (byte[])tablaRecepcion.get(destino);
									Emision emisor1 = new Emision();
									emisor1.setIp(ip);
									emisor1.setId(origen);
									
									if(tmpMessage != null)
									{
										tablaEmision.put(new Integer(origen), emisor1);
										tablaRecepcion.remove(new Integer(destino));
										
										System.arraycopy(buffer, 0, tmpMessage, 0, buffer.length);
										super.reanudarProceso(super.dameProcesoLocal(destino));
									}
									else
									{
										imprimeln("TA");
										Queue<byte[]> buzonTmp = buzones.get(destino);
										if(buzonTmp.size() < 4)
										{
											tablaEmision.put(new Integer(origen), emisor1);
											 byte[] mens = new byte[1024];
				                             System.arraycopy(buffer, 0, mens, 0, buffer.length);
				                             buzonTmp.offer(mens);
										}
										else 
										{
											despachaTA despacha = new despachaTA(buffer);
											new Thread(despacha).run();
											
											byte[] taMessage = new byte[1024];
											buffer[8] = (byte)01;
											buffer[9] = (byte)00;
											System.arraycopy(buffer, 0, taMessage, 0, buffer.length);
										}
									}
								}
								else
								{
									imprimeln("AU");
									byte[] auMessage = new byte[1024];
									buffer[8] = (byte)01;
									buffer[9] = (byte)01;
									System.arraycopy(buffer, 0, auMessage, 0, buffer.length);
								}
								
								} catch (NullPointerException e) {

								}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			try{
				sleep(60000);
			}catch(InterruptedException e){
				System.out.println("InterruptedException");
			}
		}
	}

	/*public static void recibirdeproceso(String nombre) {
		NameServer.buscarparaenviar(nombre);
	}*/
	
	public static boolean recibirdeproceso(String nombre){
		return NameServer.buscarparaenviar(nombre);
	}
	
	public class despachaTA implements Runnable
	{

		private byte[] msgTmp;
		
		public despachaTA(byte[] msgTmp) {
			this.msgTmp = msgTmp;
		}
		
		@Override
		public void run() {
			try {
				tablaTA.put(new Integer(siguienteTA), msgTmp);
				Thread.sleep(5000);
				msgTmp = (byte[])tablaTA.get(new Integer(siguienteTA));
				tablaTA.remove(new Integer(siguienteTA));
				int infoDestino = msgTmp[4];
				for(int i=5;i<8;i++)
				{
					infoDestino = (int) ((infoDestino<<8) | (msgTmp[i] & (int)0xFF));
				}
				Nucleo.send(infoDestino, msgTmp);
			} catch (Exception e) {
				
			}
		}
	}
	
}



class Emision {
	String ip;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	int id;
	String nombre;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}