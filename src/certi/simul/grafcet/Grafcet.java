package certi.simul.grafcet;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Define um Grafcet
 * 
 * Criado em 13/10/2003 
 * 
 * @author ilm
 * 
 * @see Etapa
 * @see Transicao 
 */
public class Grafcet implements Serializable {
	
	static final long serialVersionUID = -3234600802697235275L;
	
	/** Conjunto de Etapas deste grafcet */
	public final Etapa[] etapas;
	
	/** Conjunto de Transicoes deste grafcet */
	public final Transicao[] transicoes;

	public Grafcet(int[] etapasInicias, int numeroEtapas, int numeroTransicoes) {
		etapas = new Etapa[numeroEtapas];	
		transicoes = new Transicao[numeroTransicoes];
		
		//hashEtapas = new HashMap((int)(numeroEtapas* 1.5));		
		//hashTransicoes = new HashMap((int)(numeroTransicoes* 1.5));
	}
	
//	public int[] obterEtapasInicias() {
//		return 	etapasInicias;	
//	}

	public Etapa obterEtapa(int numeroEtapa) {
		for (int i = 0; i < etapas.length; i++)
			if (etapas[i].numero == numeroEtapa) 			
				return etapas[i];  
		return null; 	
	}	
	
	public Transicao obterTransicao(int numeroTransicao) {
		for (int i = 0; i < transicoes.length; i++)
			if (transicoes[i].numero == numeroTransicao) 			
				return transicoes[i];  
		return null; 	
	}
	
	public int obterNumTransicoes(){
		return 	transicoes.length;
	}

	public int obterNumEtapas(){
		return 	etapas.length;
	}	

	/**
	 * 
	 * @return
	 */
	public LinkedList obterEtapasAtivas() {
		LinkedList list = new LinkedList();
		for (int i = 0; i < etapas.length; i++) 
			if (etapas[i].estado == Etapa.ATIVA)
				list.add(new Integer(etapas[i].numero));
		return list;		
	}

	/**
	 * 
	 * @return 
	 */
	public LinkedList obterTransicoesValidas() {
		LinkedList list = new LinkedList();
		for (int i = 0; i < transicoes.length; i++) 
			if (transicoes[i].valida)
				list.add(new Integer(transicoes[i].numero));
		return list;		
	}	

	/**  */
	public String toEtapasString() {
		String s = "";		
		if (etapas != null)
			for (int i = 0; i < etapas.length; i++) 
				s += etapas[i] + System.getProperty("line.separator");								
		return s;
	}
	
	/**   */
	public String toTransicoesString() {
		String s = "";		
		if (transicoes != null)
			for (int i = 0; i < transicoes.length; i++) 
				s += transicoes[i] + System.getProperty("line.separator");;								
		return s;
	}		
	
	/*  @see java.lang.Object#toString() */
	public String toString() {
		String s = "";		
		if (etapas != null)
			for (int i = 0; i < etapas.length; i++) 
				s += etapas[i] + System.getProperty("line.separator");;
		if (transicoes != null)				
			for (int i = 0; i < transicoes.length; i++) 
				s += transicoes[i] + System.getProperty("line.separator");;								
		return s;
	}	
	
	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Grafcet))
			return false;
		Grafcet other = (Grafcet) obj;
		return Arrays.equals(this.etapas, other.etapas)
			&& Arrays.equals(this.transicoes, other.transicoes);
	}
}

