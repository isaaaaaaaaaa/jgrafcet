package certi.simul.grafcet;

/**
 * Interface que define um prototipo de Grafcet.<br> 
 * 
 * Criado em 13/10/2003 
 * 
 * @author ilm 
 */
public interface PrototipoGrafcet {
	
	/** 
	 * Prototipo deve definir um array de 'int' contendo o numero das  
	 * etapas iniciais.
	 */
	public int[] obterEtapasIniciais();
	
	/** 
	 * Prototipo deve definir um array de 'int' contendo o numero de 
	 * todas Etapas.
	 */
	public int[] obterTodasEtapas();
}
