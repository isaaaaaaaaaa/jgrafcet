package certi.simul.grafcet;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Uma Etapa de Grafcet. <br>
 * 
 * <br>Criado em 13/10/2003 
 * 
 * @author ilm 
 */
public class Etapa implements Comparable, Serializable {
	
	static final long serialVersionUID = -6709587649473179654L;
	
	// Constantes do estado da etapa 
	
	/** Constante que indica estado atual (ou proximo estado)
	 * Inativo da etapa  */	
	public static final int INATIVA = 0;
	
	/** Constante que indica estado atual (ou proximo estado)
	 * Ativo da etapa  */
	public static final int ATIVA = 1;
	
	/** Constante de simula��o que indica que a etapa n�o 
	 * mudar� de estado no pr�ximo ciclo  */
	static final int IMODIFICADA = -1;
	
	// Variaveis da estrutura 
	
	/** Valor booleano que indica que � uma etapa inicial */
	public final boolean ehInicial;
	
	/** O n�mero da etapa */
	public final int numero;
	
	/** O estado atual da etapa {Etapa.ATIVA | Etapa.INATIVA} */ 
	public int estado = Etapa.INATIVA;	
	
	/** O delay para realizar a a��o ap�s a ativa�ao da etapa (em milisegundos)*/
	int demoraAcao = 0;	
	
	/** Valor booleano que indica que se um metodo de acao � chamado continuamente
	 * ou seja, a cada ciclo do grafcet enquanto a etapa estiver acionada */
	public boolean ehAcaoContinua = false;	
	
	transient Method metodoAcao = null;
	transient Method metodoDesacao = null;	
	
	// Variaveis de simulacao 
	transient boolean acionar = false;
	transient boolean desacionar = false;
	transient int proximoEstado = Etapa.IMODIFICADA;
	
	/** En�simo ciclo (futuro) em que etapa ser� acionada. Ciclo de a��o igual
	 *  a zero, etapa ser� acionada neste ciclo. Ciclo de a��o negativo e etapa
	 *  Ativa, indica que etapa j� foi acionada */  
	int cicloAcao = -1;
	
	/**
	 * Construtor
	 * @param ehInicial � ou n�o etapa inicial
	 * @param numero o numero da etapa
	 */
	Etapa(boolean ehInicial, int numero) {
		this.ehInicial = ehInicial;
		this.numero = numero;
	}
	
	/** Inicializa�ao das vari�veis transit�rias do ciclo */
	void inicializa() {
		acionar = false;
		desacionar = false;
		proximoEstado = Etapa.IMODIFICADA;
		cicloAcao = -1;		
	}
	
	/* @see java.lang.Object#hashCode() */
	public int hashCode() {
		 return numero; 
	}
	
	/* @see java.lang.Object#toString() */
	public String toString() {
		String s = "Etapa: " + numero + " Estado: " + estado + " Demora: " + demoraAcao + " AcaoContinua: " + ehAcaoContinua;
		if (metodoAcao != null) 
			s += ", Acao: \"" + metodoAcao.getName()+"\"";
		if (metodoDesacao != null) 
			s += ", Desacao: \"" + metodoDesacao.getName()+"\"";				
		return s;
	}

	/* @see java.lang.Comparable#compareTo(java.lang.Object) */
	public int compareTo(Object arg0) {
		return ((Etapa)arg0).numero - numero;
	}		
	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Etapa))
			return false;
		Etapa other = (Etapa) obj;
		
		return this.numero == other.numero && this.estado == other.estado
			&& this.ehInicial == other.ehInicial;
	}

}