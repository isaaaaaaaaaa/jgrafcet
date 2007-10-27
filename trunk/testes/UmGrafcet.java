

import java.util.LinkedList;

import certi.simul.grafcet.PrototipoGrafcet;

/**
 * Exemplo de PrototipoGrafcet
 */
public class UmGrafcet implements PrototipoGrafcet {

	private  LinkedList disparar = new LinkedList();

	/* @see certi.util.Grafcet#obterEtapasIniciais() */
	public int[] obterEtapasIniciais() {
		return new int[]{1} ;
	}

	/* @see certi.util.Grafcet#obterTodasEtapas() */
	public int[] obterTodasEtapas() {
		return new int[]{1,2,3,4,5,6} ;
	}
	
	/*  @see certi.util.Grafcet#tratarExcecao(java.lang.Throwable) */
	public void tratarExcecao(Throwable t) {
		t.printStackTrace();
	}
	
	public void inicioCiclo() {	}

	////////////////////////////////////////////////////////////
	// Funcoes auxiliares

	synchronized private boolean receptividade(int i) {
		return disparar.contains(new Integer(i));
	}

	public void setarReceptividade(Integer integer) {
		synchronized (disparar) {
			disparar.add(integer);
		}				
	}

	public void zeraReceptividades() {
		synchronized (disparar) {
			disparar.clear();
		}				
	}


	public void fimCiclo() {
		synchronized (disparar) {
			disparar.clear();
		}
	}

	private void acao(int i) {
		System.out.println("Acao: " + i);
	}

	private void desacao(int i) {
		System.out.println("Desacao: " + i);
	}

	////////////////////////////////////////////////////////////
	// Grafcet
	
	public void acao_E_1() throws Exception {		acao(1);	}

	public void desacao_E_1() throws Exception { 	desacao(1);	}
	
	public void acao_E_2_D_3000() throws Exception {	acao(2); }
	
	public void desacao_E_2() throws Exception { 	desacao(2);}

	public void acao_E_3_C_1() throws Exception { 		acao(3);}

	public void desacao_E_3() throws Exception {	desacao(3);}
	
	public void acao_E_4() throws Exception { 		acao(4);}
	
	public void desacao_E_4() throws Exception { 	desacao(4);	}
	
	public void acao_E_5() throws Exception {  		acao(5);}
	
	public void desacao_E_5() throws Exception {	desacao(5);}	

	public void acao_E_6() throws Exception {  		acao(6);}
	
	public void desacao_E_6() throws Exception {	desacao(6);}	
	
	public boolean transicao_N_1_EA_1_EP_2(Object[] data) throws Exception { 
		return receptividade(1);
	}

	public boolean transicao_N_2_EA_1_EP_3() throws Exception { 
		return receptividade(2);
	}

	public boolean transicao_N_3_EA_2_EP_4() throws Exception { 
		return receptividade(3);
	}
	
	public boolean transicao_N_4_EA_3_EP_4(Object[] data) throws Exception { 
		return receptividade(4);
	}

	public boolean transicao_N_5_EA_4_EP_5_6() throws Exception { 
		return receptividade(5);
	}

	public boolean transicao_N_6_EA_5_6_EP_1() throws Exception { 
		return receptividade(6);
	}
}




