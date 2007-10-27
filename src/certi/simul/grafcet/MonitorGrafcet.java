package certi.simul.grafcet;

/**
 * Interface de monitoramento da execu��o de um grafcet.<br>
 *    
 * <br>Criado em 25/11/2003 
 * 
 * @author ilm 
 */
public interface MonitorGrafcet {
	
	/**
	 * @param num
	 */
	public void etapaAcionada(int num);
	
	/**
	 * @param ativa
	 * @param num
	 */
	public void etapaAtiva(boolean ativa, int num);	
	
	/**
	 * @param num o numero da transicao
	 */
	public void transicaoDisparada(int num);
	
	/**
	 * @param iniciado
	 */
	public void cicloIniciado(boolean iniciado);	
	
	/**
	 * @param t a exece��o lan�ada
	 * @param etapa true se for na execu��o de uma etapa, false se for na 
	 *        execu��o de uma transicao
	 * @param num o n�mero da etapa/transi��o
	 */
	public void tratarExcecao(Throwable t, boolean etapa, int num);
}
