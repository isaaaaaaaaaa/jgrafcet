import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import certi.simul.grafcet.ExcecaoNaValidacao;
import certi.simul.grafcet.InterpretadorGrafcet;

/**
 * 
 * @author ilm 
 */
public class TesteGrafcet {
	
	/** 	 */
	public static void main(String[] args) {
		UmGrafcet g = new UmGrafcet();
		InterpretadorGrafcet motor = null;

		try {
			motor = new InterpretadorGrafcet(g);
			motor.setarTempoCicloMs(100);
			
			// Inicia Thread
			Thread thread = new Thread(motor);
			thread.start();
		} catch (ExcecaoNaValidacao e) {		
			e.printStackTrace();
			System.exit(1);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println();
		System.out.println("Digite 'e' para imprimir Etapas, 't' para transicoes, 'r' para reset, 'z' zerar receptividades.");  
		System.out.println("Ou digite o numero das receptividades do ciclo, separadas por espaco. Ex. '1 3'");
		System.out.println();

		// Inicia leitura de comandos		
		while (true) {
			String line;
			try {
				line = reader.readLine();
				String[] os = line.split(" ");
				for (int i = 0; i < os.length; i++) {
					if (os[i].equals("e"))
						System.out.println(motor.obterGrafcet().toEtapasString());
					else if (os[i].equals("t"))
						System.out.println(motor.obterGrafcet().toTransicoesString());						
					else if (os[i].equals("r"))
						motor.reset();	
					else if (os[i].equals("z"))
						g.zeraReceptividades();											
					else  {
						try {
							g.setarReceptividade(new Integer(os[i]));
						} catch (NumberFormatException e) {	}
					}						 
				}										
				
			} catch (IOException e1) { 	e1.printStackTrace(); }								
		}
	}
}