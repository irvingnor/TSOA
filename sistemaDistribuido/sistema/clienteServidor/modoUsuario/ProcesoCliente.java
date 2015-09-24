/*
	Alumna:  Guerrero Muñoz Silvia Veronica Guadalupe
	Sección: 03
	practica 5
*/

package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.visual.util.PanelIPID;

/**
 * 
 */
public class ProcesoCliente extends Proceso{
	private static short codop=0;
	private static String mensaje;
	public static String ipserver;
	public static int idserver;
	static String nombreserver;
	/**
	 * 
	 */
	public ProcesoCliente(Escribano esc){
		super(esc);
		start();		
	}
	
	public static void datosServidor(datosServidor ds){
	ipserver = ds.getIp();
	idserver = ds.getIdprocess();
	nombreserver = ds.getName();
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	public void run(){
		
		imprimeln("Proceso cliente en ejecucion.");
		imprimeln("\nEsperando datos para continuar.");
		Nucleo.suspenderProceso();
		int guardaid = dameID();
		byte[] solCliente=new byte[1024];
		byte[] respCliente=new byte[1024];
		solCliente[0]=(byte)(guardaid);
		solCliente[1]=(byte)(guardaid>>8);
		solCliente[2]=(byte)(guardaid>>16);
		solCliente[3]=(byte)(guardaid>>24);   
		solCliente[4]=(byte)(248);
		solCliente[5]=(byte)(248>>8);
		solCliente[6]=(byte)(248>>16);
		solCliente[7]=(byte)(248>>24); 
		short codop = this.getCodop();
		solCliente[8]=(byte)(getCodop());
		solCliente[9]=(byte)(getCodop()>>8);  
		imprimeln("Codigo De Operacion: "+codop);
		String mesagge = this.getMensaje();
		byte[] byteacadena = mesagge.getBytes();
		int contador = 10;
		for(int j=0; j<mesagge.length(); j++){
		solCliente[contador] = byteacadena[j];	
		contador++;
		}
		String nombre = PanelIPID.damenombre();
		imprimeln("\nQuiero enviar mi mensaje al servidor: "+nombre);
		boolean seguir = MicroNucleo.recibirdeproceso(nombre);
		//Nucleo.send(nombre, message);
		if (seguir){
		imprimeln("\nEl servidor si existe!!!\n");
		imprimeln("\nNombre Servidor: "+nombreserver+" ID: "+idserver+" Ip: "+ipserver);
		Nucleo.send(248,solCliente);
		Nucleo.receive(dameID(),respCliente);
		imprimeln("\nRespuesta del servidor: \n");
		char temporal;
		for(int j=8; j<1016; j++){
		temporal = (char) respCliente[j];
		imprimeln(String.valueOf(temporal));
		}
		//Una vez enviado el mensaje se procede a eliminar el servidor del name server
		NameServer.deregistro(idserver);
		} else {
			imprimeln("\nEl servidor no existe :(");
		}
	}

	public static short getCodop() {
		return codop;
	}

	public static void setCodop(short codop) {
		ProcesoCliente.codop = codop;
	}

	public String getMensaje() {
		return mensaje;
	}

	public static void setMensaje(String mensaje) {
		ProcesoCliente.mensaje = mensaje;
	}
	
}
