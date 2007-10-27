package certi.simul.grafcet;

/**
 * Interface de monitoramento da execução de um grafcet.<br>
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
	 * @param t a execeção lançada
	 * @param etapa true se for na execução de uma etapa, false se for na 
	 *        execução de uma transicao
	 * @param num o número da etapa/transição
	 */
	public void tratarExcecao(Throwable t, boolean etapa, int num);
}
