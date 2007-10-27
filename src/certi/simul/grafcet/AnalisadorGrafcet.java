package certi.simul.grafcet;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe que analisa um <tt>PrototipoGrafcet</tt> transformando-o em um
 * objeto da classe  <tt>Grafcet</tt>. <br>
 *  
 * <br>Criado em 13/10/2003 
 * 
 * @author ilm 
 * 
 * @see certi.simul.grafcet.PrototipoGrafcet
 */
public class AnalisadorGrafcet {

	/** Flag que indica se grafcet foi validado */
	private boolean validado = false;
	/** O grafcet analisado */
	private final Grafcet graf;
	/** O prototipo analisado */
	private final PrototipoGrafcet pgrafcet;

	/**
	 * Construtor
	 * @param pgrafcet o PrototipoGrafcet a analisar
	 */
	public AnalisadorGrafcet(PrototipoGrafcet pgrafcet) {
		super();
		this.pgrafcet = pgrafcet;
				
		int numTrasicoes = 0;
		Method[] metodos = pgrafcet.getClass().getMethods();
		for (int i = 0; i < metodos.length; i++) 
			if (metodos[i].getName().startsWith("transicao_"))
				++numTrasicoes;
		int numEtapas = pgrafcet.obterTodasEtapas().length;
				
		graf = new Grafcet(pgrafcet.obterEtapasIniciais(), numEtapas, numTrasicoes);			
	}

	/**
	 * Faz o procedimento de validacao do PrototipoGrafcet.
	 * 
	 * @throws ExcecaoNaValidacao
	 */
	public void validar() throws ExcecaoNaValidacao {
		
		if (validado)
			return;
				
		int[] todasEtapas = pgrafcet.obterTodasEtapas();
		
		// Verifica se tem ao menos uma etapa inicial
		if (pgrafcet.obterEtapasIniciais() != null) {
			if (pgrafcet.obterEtapasIniciais().length == 0)
				throw new ExcecaoNaValidacao("Etapas iniciais nao definidas.");
		} else
			throw new ExcecaoNaValidacao("Etapas iniciais nao definidas.");
		
		// Verifica se tem ao menos uma etapa
		if (todasEtapas != null) {
			if (todasEtapas.length == 0)
				throw new ExcecaoNaValidacao("Nenhuma etapa definida.");
		} else
			throw new ExcecaoNaValidacao("Nenhuma etapa definida.");			
		
		// Verifica se trasicoes sao validas		
		gerarEtapasTransicoes();

		validado = true;				
	}

	/**
	 * 
	 */
	private void gerarEtapasTransicoes() throws ExcecaoNaValidacao {

		// Obtem medotos definidos no prototipo 
		Method[] metodos = pgrafcet.getClass().getMethods();
		if (metodos == null)
			throw new ExcecaoNaValidacao("Nenhum metodo fornecido");
					
		// Lista encadeada para metodos de transicao, acao e desacao
		LinkedList metTransicoes = new LinkedList();
		LinkedList metAcoes = new LinkedList();
		LinkedList metDesacoes = new LinkedList();
		
		for (int i = 0; i < metodos.length; i++) {
			if (metodos[i].getName().startsWith("transicao_"))
				metTransicoes.add(metodos[i]);
			if (metodos[i].getName().startsWith("acao_"))
				metAcoes.add(metodos[i]);
			if (metodos[i].getName().startsWith("desacao_"))
				metDesacoes.add(metodos[i]);				 			
		}
		
		// Inicializa etapas
		int numEtapas = pgrafcet.obterTodasEtapas().length;
		int[] todasEtapas = pgrafcet.obterTodasEtapas();
		boolean ehInicial;		
		//Etapa umaEtapa;
		for (int i = 0; i < numEtapas; i++) {
			ehInicial = false;

			// Seta etapas inicias
			for (int j = 0; j < pgrafcet.obterEtapasIniciais().length; j++) 
				if (pgrafcet.obterEtapasIniciais()[j] == todasEtapas[i]) 
					ehInicial = true;
				
			graf.etapas[i] = new Etapa(ehInicial, todasEtapas[i] /*numero*/);
			graf.etapas[i].estado = (ehInicial ? Etapa.ATIVA : Etapa.INATIVA);								
		}					

		// Monta objetos de transicao		
		Method met = null;		
		boolean error = false;	
		String errorMsg = "";
		Iterator iter;
		try {
			
			// Tokens de identificacao
			String[] charTokens = {"E", "D", "C"};
			List lTokens = Arrays.asList(charTokens);
			
			// Para cada metodo de acao		
			iter = metAcoes.iterator();			
			for (int i = 0; iter.hasNext() && !error; i++) {				
				met = (Method)iter.next();			
				
				// Separa numeros entre as Strings "E D"
				StringTokenizer st = new StringTokenizer(
					met.getName().replaceFirst("acao_", ""), "_");
				
				// Etapa atual
				Etapa et = null; 	
				// Obtem os pares <ID>_<VALOR> (i.e. <ID1>_<VALOR1>_<ID2>_<VALOR2>...)
				while (st.hasMoreTokens()) {				
					// Obtem token de identificacao 
					String id = st.nextToken();
					// Verifica se identificador eh válido 
					if (!lTokens.contains(id.toUpperCase())) {
						error = true;
						errorMsg = "Identificador '" + id  + "' nao válido";
						break;						
					}
					
					// Obtem valor do item
					String valor;
					if (st.hasMoreElements())
						valor = st.nextToken();
					else {
						error = true;
						errorMsg = "Valor de '" + id  + "' nao definido";
						break;						
					}					
					
					// Etapa
					if (id.toUpperCase().equals("E")) {					
						// Obtem numero da etapa, q deve corresponder ao primeiro token
						int[] numEtapa = {Integer.parseInt(valor)};
						
						// Verifica se etapa esta definida				
						if (!etapasDefinidas(numEtapa)) {
							error = true;
							errorMsg = "Etapa " + numEtapa[0] + " nao definida";
							break;
						}
						
						// Seta metodo de acao na etapa
						et = graf.obterEtapa(numEtapa[0]);
						et.metodoAcao = met;
					}
					
					// Obtem a demora, se existir 
					if (id.toUpperCase().equals("D")) {
						if (et != null)	
							et.demoraAcao = Integer.parseInt(valor);
						else {
							error = true;
							errorMsg = "Identificador 'E' dever se o primeiro";
							break;								
						}
					}						
								
					// Obtem a flag q indica etapa continua
					if (id.toUpperCase().equals("C")) {
						if (et != null)	
							et.ehAcaoContinua = "1".equals(valor);
						else {
							error = true;
							errorMsg = "Identificador 'E' dever se o primeiro";
							break;								
						}
					}	
				}	
			}

			if (error)
				throw new ExcecaoNaValidacao("Funcao de acao invalida: \"" + met.getName() + "\" Msg: " + errorMsg);
			
			//	Para cada metodo de desacao
			iter = metDesacoes.iterator();			
			for (int i = 0; iter.hasNext(); i++) {

				// Seta funcao booleana de receptividade da transicao
				met = (Method)iter.next();			
				
				// Obtem etapa da desacao
				int[] numEtapa = {Integer.parseInt(met.getName().replaceFirst("desacao_E_", ""))};
				
				// Verifica se etapa esta definida
				if (!etapasDefinidas(numEtapa)) {
					error = true;
					errorMsg = "Etapa " + numEtapa[0] + " nao definida";
					break;
				}
				
				// Seta metodo de desacao na etapa
				Etapa et = graf.obterEtapa(numEtapa[0]);
				et.metodoDesacao = met;
			}			
			if (error)
				throw new ExcecaoNaValidacao("Funcao de desacao invalida: \"" + met.getName() + "\" Msg: " + errorMsg);
							
			// Para cada metodo de transicao
			iter = metTransicoes.iterator();			
			for (int i = 0; iter.hasNext(); i++) {

				// Seta funcao booleana de receptividade da transicao
				met = (Method)iter.next();			
				
				// Separa numeros entre as Strings "N EA EP"
				StringTokenizer st = new StringTokenizer(
					met.getName().replaceFirst("transicao_N_", ""), "N EA EP");
				// Verifica se todos campos foram definidos ("N EA EP")
				if (st.countTokens() == 3) {
					// Obtem numero da transicao, q deve corresponder ao primeiro token
					final int numero = Integer.parseInt(st.nextToken().split("_")[0]);
					// Inicializao objeto Transicao
					graf.transicoes[i] = new Transicao(numero);
					graf.transicoes[i].receptividade = met;
					
					// Obtem etapas anteriores
					StringTokenizer stEtapasAnter = new StringTokenizer(st.nextToken(), "_");
					int[] etapasAnteriores = new int[stEtapasAnter.countTokens()];
					if (etapasAnteriores.length != 0) {
						// Se alguma etapa anterior definida, entao obte-la(s)
						for (int j = 0; stEtapasAnter.hasMoreTokens(); j++)  
							etapasAnteriores[j] = 
								Integer.parseInt(stEtapasAnter.nextToken());
						// Verifica se etapas anteriores estao definidas (existem)
						if (!etapasDefinidas(etapasAnteriores)) {
							error = true;
							errorMsg = "Etapas anteriores nao definidas";
							break;
						}
					
						// Seta obj etapasAnteriores na transicao
						graf.transicoes[i].etapasAnteriores = new Etapa[etapasAnteriores.length];
						for (int j = 0; j < etapasAnteriores.length; j++) 
							for (int k = 0; k < graf.etapas.length; k++) 
								if (etapasAnteriores[j] == graf.etapas[k].numero) {
									graf.transicoes[i].etapasAnteriores[j] = graf.etapas[k];
									continue;
								}								
														
					} else {
						error = true;
						errorMsg = "Etapas anteriores nao definidas";
						break;
					}
					
					// Obtem etapas posteriores
					StringTokenizer stEtapasPoster = new StringTokenizer(st.nextToken(), "_");
					int[] etapasPosteriores = new int[stEtapasPoster.countTokens()]; 
					if (etapasPosteriores.length != 0) {
						// Se alguma etapa anterior definida, entao obte-la(s)
						for (int j = 0; stEtapasPoster.hasMoreTokens(); j++)
							etapasPosteriores[j] = 
								Integer.parseInt(stEtapasPoster.nextToken());	
						// Verifica se etapas posteriores estao definidas (existem)
						if (!etapasDefinidas(etapasPosteriores)) {
							error = true;
							errorMsg = "Etapas posteriores nao definidas";
							break;
						}	
						
						// Seta obj etapasPosteriores na transicao
						graf.transicoes[i].etapasPosteriores = new Etapa[etapasPosteriores.length];
						for (int j = 0; j < etapasPosteriores.length; j++) 
							for (int k = 0; k < graf.etapas.length; k++) 
								if (etapasPosteriores[j] == graf.etapas[k].numero) {
									graf.transicoes[i].etapasPosteriores[j] = graf.etapas[k];
									continue;
								}																		
					} else {
						error = true;
						errorMsg = "Etapas posteriores nao definidas";
						break;
					}
					
				} else {
					error = true;
					errorMsg = "Campos 'E' 'EA' 'EP' nao definidos";
					break;
				}					
			}
			
			if (error)
				throw new ExcecaoNaValidacao("Funcao de transicao invalida: \"" + met.getName() + "\" Msg: " + errorMsg);			
			
		} catch (NumberFormatException  e) {
			throw new ExcecaoNaValidacao("Funcao de invalida: \"" + met.getName() + "\" Msg: " + errorMsg);
		}				
	}

	/**
	 * Codigo macabro pra ver se um array de etapas esta definida em "todasEtapas" (do grafcet)
	 * @param is
	 * @return
	 */
	private boolean etapasDefinidas(int[] is) {
		boolean todasDefinidas = true;
		if (is != null) {
			boolean[] etapaDefinida = new boolean[is.length];			
			for (int i = 0; i < is.length; i++) {
				etapaDefinida[i] = false; 
				for (int j = 0; j < graf.etapas.length; j++) {
					// Se etapa i do array esta em todasEtapas, entao esta existe
					etapaDefinida[i] |=  (is[i] == graf.etapas[j].numero);
				}
				todasDefinidas &= etapaDefinida[i];
			}
		}
		return todasDefinidas;
	}	

	/**
	 * @return true se ja foi validado, false se nao
	 */
	public boolean estaValidado() {
		return validado;
	}

	/**
	 * @return o Grafcet analisado
	 */
	public Grafcet obterGrafcet() {
		if (!validado)
			throw new RuntimeException("Grafcet não validado!");
		return graf;
	}

	/**
	 * @return o Prototipo analisado
	 */
	public PrototipoGrafcet obterPrototipo() {
		return pgrafcet;
	}	
}
