/*
 * Llamas Covarrubias Irving Norehem
 * 206709311
 */
package proyectoFinal.irving;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	
	public void create(String msg)
	{
		try {
			Path target = Paths.get(msg);
			@SuppressWarnings("unused")
			Path file = Files.createFile(target);
		} catch (Exception e) {
		}
	}
	
	public void delete(String msg)
	{
		try {
			Path target = Paths.get(msg);
			Files.deleteIfExists(target);
		} catch (Exception e) {
		}
	}
	
	public void write(String msg,String info)
	{
		FileWriter fichero = null;
        PrintWriter pw = null;
		try {
			fichero = new FileWriter(msg);
			pw = new PrintWriter(fichero);
			
			for(int i=0;i<info.length();i++)
			{
				pw.print(info.charAt(i));
			}
			//pw.println(info);
		} catch (Exception e) {
		}
		finally {
	           try {
	           if (null != fichero)
	              fichero.close();
	           } catch (Exception e2) {
	              e2.printStackTrace();
	           }
	        }
	}
	
	public void run(){
		imprimeln("Proceso servidor en ejecucion.");
		Nucleo.dameBuzon(super.dameID());//Pedimos buzon
		imprimeln("Buzon asignado correctamente");
		byte[] solServidor=new byte[1024];
		byte[] respServidor;
		//byte dato;
		while(continuar()){
			Nucleo.receive(dameID(),solServidor);
			//dato=solServidor[0];
			//imprimeln("el cliente envia un "+dato);
			respServidor=new byte[1024];
			
			
			String msg = "";
			try {
				msg =  new String(solServidor,0,solServidor.length);
				int limite = (byte)solServidor[10];
				msg = msg.substring(11, 11+limite);
			} catch (Exception e) {
				imprimeln("Fallo"+e.toString()+"\n");
			}			
			
			String answer = "El archivo ";
			
			int indice = 0;
			boolean encontrado = false;
			String informacion = "";
			for(;!encontrado && indice < msg.length();indice++)
			{
				if(msg.charAt(indice) == '|')
				{
					encontrado = true;
				}
			}
			
			if(encontrado)
			{
				informacion = msg.substring(indice, msg.length());
				msg = msg.substring(0, indice-1);
			}
			
			answer += msg;
			
			answer += " fue ";
			if(solServidor[8]==(byte)00 && solServidor[9] == (byte)01)
			{
				imprimeln("Peticion crear "+msg);
				answer += "creado satisfactoriamente";
				create(msg);
			}
			else if(solServidor[8]==(byte)00 && solServidor[9] == (byte)02)
			{
				imprimeln("Peticion eliminar "+msg);
				answer += "eliminado correctamente";
				delete(msg);
			}
			else if(solServidor[8]==(byte)00 && solServidor[9] == (byte)03)
			{
				imprimeln("Peticion leer "+msg);
				answer += "leido correctamente\n";
				
			    FileReader fr = null;
			    BufferedReader br = null;
				try {
					fr = new FileReader(msg);
					br = new BufferedReader(fr);
					 String linea;
			         while((linea=br.readLine())!=null)
			            answer+=linea+"\n";
				} catch (Exception e) {
					imprimeln("Error al leer archivo");
				}finally
				{
					try{                    
			            if( null != fr ){   
			               fr.close();     
			            }                  
			         }catch (Exception e2){ 
			            e2.printStackTrace();
			         }
				}
				
			}
			else if(solServidor[8]==(byte)00 && solServidor[9] == (byte)04)
			{
				imprimeln("Peticion escribir "+msg);
				answer += "escrito correctamente";
				write(msg,informacion);
			}
			
			respServidor[8] = (byte)answer.length();
			byte[] tmpAmswer = answer.getBytes();
			for(int i=0;i<answer.length();i++)
			{
				respServidor[i+9] = tmpAmswer[i];
			}
			
			int infoDestino = 0;
			infoDestino = solServidor[4];
			for(int i=5;i<8;i++)
			{
				infoDestino = (int) ((infoDestino<<8) | (solServidor[i] & (int)0xFF));
			}
			
			int infoOrigen = solServidor[0];
			for(int i=1;i<4;i++)
			{
				infoOrigen = (int) ((infoOrigen<<8) | (solServidor[i] & (int)0xFF));
			}
			//respServidor[0]=(byte)(dato*dato);
			Pausador.pausa(1000);  //sin esta l�nea es posible que Servidor solicite send antes que Cliente solicite receive
			imprimeln("enviando respuesta");
			
			Nucleo.send(infoOrigen,respServidor);
		}
	}
}
