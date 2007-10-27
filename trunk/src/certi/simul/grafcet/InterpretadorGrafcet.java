package certi.simul.grafcet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * 
 * Created on Oct 11, 2003
 * 
 * @author ilm 
 */
public class InterpretadorGrafcet implements Runnable {
	
	/** Flag que indica se passo de inicializacao foi realizado */
	private boolean iniciado = false;
	/** Intervalo entre cada passo de execucao, usado pela Thread */	 
	private volatile int tempoCicloMs = 1000;
	/** Intervalo entre cada passo de execucao, usado pela Thread */
	private AnalisadorGrafcet analisador;
	private Transicao[] transicoes;
	private Etapa[] etapas;
	
	/** Objetos passados as transicoes em somente um ciclo */
	private LinkedList dadoCiclo = new LinkedList(); 
	
	/** Array objetos passados as transicoes em somente um ciclo */
	private Object[] dadoCicloArray = new Object[1];
	
	/** Monitor do grafcet */
	private final MonitorGrafcet monitor;
	
	/** Construtor */ 
	public InterpretadorGrafcet(PrototipoGrafcet grafcet) throws ExcecaoNaValidacao {
		this(grafcet, null);		
	}
	
	/** Construtor */ 
	public InterpretadorGrafcet(PrototipoGrafcet grafcet, MonitorGrafcet m) 
		throws ExcecaoNaValidacao {		
		analisador =  new AnalisadorGrafcet(grafcet);
		analisador.validar();
		
		etapas = analisador.obterGrafcet().etapas;
		transicoes = analisador.obterGrafcet().transicoes;
		monitor = m;		
	}	
	
	/////////////////////////////////////////////////////////////////
	/// MOTOR DE EXECUCAO
	
	/** Primeiro passo: aciona etapas inicias */
	private boolean inicializacao() {
		
		if (!analisador.estaValidado()) {
			try {
				analisador.validar();
				etapas = analisador.obterGrafcet().etapas;
				transicoes = analisador.obterGrafcet().transicoes;
			} catch (ExcecaoNaValidacao e) { 
				e.printStackTrace();
				return false;
			}			
		}
		
		// Para cada etapa do grafcet
		for (int i = 0; i < etapas.length; i++) {
			// Se etapa eh inicial e tem funcao de acao definida			
			if (etapas[i].ehInicial) {
				etapas[i].estado = Etapa.ATIVA;	
				if (monitor != null)
					monitor.etapaAtiva(true, etapas[i].numero);			
				// Chama metodo de acao se existir
				chamarAcao(true, etapas[i]);				
			} else
				etapas[i].estado = Etapa.INATIVA;
			etapas[i].inicializa();							  
		}		 
		return iniciado = true; 
	}
	
	/** Realiza um ciclo do grafcet */
	synchronized public boolean passo() {
		boolean ok = true;
		
		if (monitor != null)
			monitor.cicloIniciado(true);
		
		// Faz inicializacao das etapas iniciais no primeiro passo
		/* Regra 1 GRAFCET */
		if (!iniciado) 			
			return inicializacao(); 
				
		// Seta dados disponiveis para transicoes neste ciclo
		synchronized (dadoCiclo) {
			if (dadoCiclo.size() > 0) {
				dadoCicloArray[0] =  dadoCiclo.toArray(new Object[dadoCiclo.size()]);
				dadoCiclo.clear();
			}				
		}
		
		// Para cada transicao validar e setar flag de disparo
		for (int i = 0; i < transicoes.length; i++) {
			// Flag de Validacao eh setado se etapas anterios estao ativas
			transicoes[i].valida = 
				etapasAtivas(transicoes[i].etapasAnteriores);
			
			// Chama funcao de receptividade da transicao
			boolean receptividade = false;
			if (transicoes[i].valida)
				receptividade = chamarReceptividade(transicoes[i]);
			
			// Se receptividade eh verdadeira e transicao eh valida, setar flag de disparo
			/* Regra 2 GRAFCET */
			transicoes[i].disparar = 
				receptividade && transicoes[i].valida;
			
			// Para cada transicao disparada, desativar etapas precedentes
			// e ativar etapas seguintes. 
			/* Regra 3,4 GRAFCET */	
			if (transicoes[i].disparar) {

				// Ativa estapas posteriores	
				for (int j = 0; j < transicoes[i].etapasPosteriores.length; j++) 
					transicoes[i].etapasPosteriores[j].proximoEstado = Etapa.ATIVA;

				// Desativa etapas anteriores
				for (int j = 0; j < transicoes[i].etapasAnteriores.length; j++) {
					int estadoEtapa = transicoes[i].etapasAnteriores[j].proximoEstado;
					// So desativa, se ja nao tiver sido ativada
					/* Regra 5 GRAFCET */
					if (estadoEtapa != Etapa.ATIVA)
						transicoes[i].etapasAnteriores[j].proximoEstado = Etapa.INATIVA;
				}								
			}			
			if (transicoes[i].disparar && monitor != null)
				monitor.transicaoDisparada(transicoes[i].numero);					
		}
		
		// Desativa e Ativa Acoes das etapas			
		/* Regra 3 GRAFCET */
		for (int i = 0; i < etapas.length; i++) {
			// Desativa etapas
			if ( etapas[i].estado == Etapa.ATIVA && etapas[i].proximoEstado == Etapa.INATIVA ) {
				etapas[i].estado = Etapa.INATIVA;
				
				//Se etapa foi realmente acionada (ciclo de acao negativo na transicao ATIVO->INATIVO)
				// entao setar flag de desacao
				if (etapas[i].cicloAcao < 0)
					etapas[i].desacionar = true;

				if (monitor != null)
					monitor.etapaAtiva(false, etapas[i].numero);									
			}
		}
		
		for (int i = 0; i < etapas.length; i++) {
			// Ativacao da Etapa
			if (etapas[i].estado == Etapa.INATIVA && etapas[i].proximoEstado == Etapa.ATIVA ) {
				etapas[i].estado = Etapa.ATIVA;
				etapas[i].cicloAcao = (etapas[i].demoraAcao / tempoCicloMs);
				
				if (monitor != null)
					monitor.etapaAtiva(true, etapas[i].numero);  
			}		
			
			// Acoes Retardadas
			if (etapas[i].estado == Etapa.ATIVA) {
				if (etapas[i].cicloAcao == 0) { 		// Se ciclo de acao eh o atual, acionar
					// Seta flag de acionamento
					etapas[i].acionar = true;									  
				} else if (etapas[i].cicloAcao < 0)     // Ciclo de acao negativo indica que ja acionou 
					if (etapas[i].ehAcaoContinua) 	
						etapas[i].acionar = true;													
				//} else if (etapas[i].cicloAcao > 0) { 	// Se acao esta retardada (ciclo de acao futuro), decrementar
									
				etapas[i].cicloAcao--;														
			}			 
		}
		
		// Chama funcoes de acao/desacao
		for (int i = 0; i < etapas.length; i++) 
			if (etapas[i].desacionar)
				chamarAcao(false, etapas[i]);
		for (int i = 0; i < etapas.length; i++) 
			if (etapas[i].acionar)
				chamarAcao(true, etapas[i]);			
		
		// Prepara proximo ciclo
		for (int i = 0; i < etapas.length; i++) {
			etapas[i].proximoEstado = Etapa.IMODIFICADA;
			etapas[i].acionar = false;
			etapas[i].desacionar = false;
		}
		
		if (monitor != null)
			monitor.cicloIniciado(false);
		
		// Limpa objeto
		dadoCicloArray[0] = null;	
				
		return ok;
	}

	/**
	 * 
	 */
	public void aguardar() {
		synchronized (this) {
			try {
				this.wait(tempoCicloMs);
			} catch (InterruptedException e) {	}	
		}		
	}

	/**
	 * @param etapas
	 * @return true se todas etapas ativas no array
	 */
	private boolean etapasAtivas(Etapa[] etapas) {
		boolean ativas = true;
		for (int i = 0; i < etapas.length; i++) 
			ativas &= (etapas[i].estado == Etapa.ATIVA); 			

		return ativas;
	}

	/**
	 * @param transicao
	 * @return boolean o valor da receptividade
	 */
	private boolean chamarReceptividade(Transicao transicao) {
		Method method = transicao.receptividade;
		Class[] param = method.getParameterTypes();
		try {
			// Se nao tem parametros
			if (param.length == 0) {
				Boolean b = (Boolean) method.invoke(analisador.obterPrototipo(), null);
				return b.booleanValue();
			}			
			
			// Se tem um parametro, passar dados para transicao	
			if (param.length == 1) {
				Boolean b = (Boolean)method.invoke(analisador.obterPrototipo(), dadoCicloArray);
				return b.booleanValue(); 
			}												
						
		} catch (IllegalArgumentException e) {	e.printStackTrace();
		} catch (IllegalAccessException e) {	e.printStackTrace();
		} catch (InvocationTargetException e) {
			// Direciona excecao ao grafcet
			if (monitor != null)
				monitor.tratarExcecao(e, false, transicao.numero);
		}	
		return false;
	}

	/**
	 * 
	 * @param acao
	 * @param etap
	 */
	private void chamarAcao(boolean acao, Etapa etap) {
		Method method = (acao) ? etap.metodoAcao : etap.metodoDesacao;
		if (method == null)
			return; 		
		Class[] param = method.getParameterTypes();
		try {
			
			if (monitor != null && acao)
				monitor.etapaAcionada(etap.numero);
			
			// Se nao tem parametros
			if (param.length == 0) {		
				method.invoke(analisador.obterPrototipo(), null);
			
			// Se tem um parametro, passar dados para transi
			} else if (param.length == 1) {
				method.invoke(analisador.obterPrototipo(), dadoCicloArray);
				
			} else
				throw new IllegalArgumentException("Número de argumentos inválidos na função de ação ou desação: " 
					+ param.length + ", etapa: " + etap.numero);								
						
		} catch (IllegalArgumentException e) {	e.printStackTrace();
		} catch (IllegalAccessException e) {	e.printStackTrace();
		} catch (InvocationTargetException e) {
			// Direciona excecao ao grafcet
			if (monitor != null)
				monitor.tratarExcecao(e, true, etap.numero);
		}	
	}	

	/**
	 * Reseta grafcet, desacionando etapas ativas
	 */
	synchronized public void reset() {

		// Faz inicializacao das etapas iniciais no primeiro passo
		if (!iniciado) 			
			return; 
		
		// Desativa Acoes das etapas			
		for (int i = 0; i < etapas.length; i++) {
			// Desacoes
			if ( etapas[i].estado == Etapa.ATIVA ) {
				// Se etapa foi realmenta acionada (ciclo de acao negativo na transicao ATIVO->INATIVO)
				if (etapas[i].cicloAcao < 0)
					etapas[i].desacionar = true;	
				
				if (monitor != null)
					monitor.etapaAtiva(false, etapas[i].numero);			
			}
		}
		
		// Chama funcoes de acao/desacao
		for (int i = 0; i < etapas.length; i++) { 
			if (etapas[i].desacionar)
				chamarAcao(false, etapas[i]);
		}
		iniciado = false;	
	}
	
	/* @see java.lang.Runnable#run()  */
	public void run() {
				
		while (true) {
			
			// Executa passo da simulacao
			passo();
			
			// Aguarda o tempo de ciclo
			synchronized (this) {
				try {
					this.wait(tempoCicloMs);
				} catch (InterruptedException e) {
					return;
				}	
			}	
		}
	}

	////////////////////////////////////////////////////////////////////////
	//  METODOS ACESSORES

	/**
	 * @return int o tempo de ciclo em milisegundos
	 */
	public int obterTempoCicloMs() {
		return tempoCicloMs;
	}

	/**
	 * @return Grafcet o grafcet associado ao motor
	 */
	public Grafcet obterGrafcet() {
		return analisador.obterGrafcet();
	}

	/**
	 * Seta o tempo de ciclo, este tempo nao pode ser igual a zero. O
	 * mínimo recomendado eh de 50ms
	 * @param tc
	 */
	public void setarTempoCicloMs(int tc) {
		if (tc <= 0)
			tempoCicloMs = 50;
		else
			tempoCicloMs = tc;
	}
	
	/**
	 * @param obj o objeto de dado a ser adicionado ao ciclo
	 */
	public void adicionarDadoCiclo(Object obj) {
		synchronized (dadoCiclo) {
			dadoCiclo.add(obj);		
		}	
	}

	/**
	 * @param objs 
	 */
	public void adicionarDadoCiclo(Object[] objs) {
		synchronized (dadoCiclo) {
			for (int i = 0; i < objs.length; i++) 
				dadoCiclo.add(objs[i]);								
		}	
	}
	
	/**
	 * @return AnalisadorGrafcet
	 */
	public AnalisadorGrafcet obterAnalisador() {
		return analisador;
	}

}



