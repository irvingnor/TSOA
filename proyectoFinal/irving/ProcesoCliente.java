/*
 * Llamas Covarrubias Irving Norehem
 * 206709311
 */
package proyectoFinal.irving;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

/**
 * 
 */
public class ProcesoCliente extends Proceso{

	/**
	 * 
	 */
	public ProcesoCliente(Escribano esc){
		super(esc);
		start();
	}

	private int opcion;
	private String msg;
	
	public void setOpcion(int opcion,String msg)
	{
		this.opcion = opcion;
		this.msg = msg;
	}
	
	
	/**
	 * 
	 */
	public void run(){
		imprimeln("Proceso cliente en ejecucion.");
		imprimeln("Esperando datos para continuar.");
		Nucleo.suspenderProceso();
		//imprimeln("Hola =)");
		byte[] solCliente=new byte[1024];
		byte[] respCliente=new byte[1024];
		//byte dato;
		solCliente[8]=(byte)00;
		
		switch(opcion)
		{
		case 0:
			solCliente[9]=(byte)01;
			break;
		case 1:
			solCliente[9]=(byte)02;
			break;
		case 2:
			solCliente[9]=(byte)03;
			break;
		case 3:
			solCliente[9]=(byte)04;
			break;
		default:
		}
		
		byte[] info;
		
		info = msg.getBytes();
		
		solCliente[10] = (byte)info.length;
		
		for(int i=0;i<info.length;i++)
		{
			solCliente[i+11] = info[i];
		}
		
		Nucleo.send(248,solCliente);
		Nucleo.receive(dameID(),respCliente);
		//dato=respCliente[0];
		String mensaje = "";
		try {
			mensaje = new String(respCliente,0,respCliente.length);
			int limite = (byte)respCliente[8];
			mensaje = mensaje.substring(9,9+limite);
		} catch (Exception e) {
			// TODO: handle exception
		}
		imprimeln("El servidor me envia un: "+mensaje);
	}
}
