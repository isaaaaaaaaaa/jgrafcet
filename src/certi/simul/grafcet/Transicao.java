/*
 * Criado em 13/10/2003
 *
 */
package certi.simul.grafcet;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * Uma Transição de Grafcet. <br>
 *   
 * <br>Criado em 13/10/2003 
 * 
 * @author ilm 
 * 
 * @see Etapa
 */
public class Transicao implements Comparable, Serializable {
	
	static final long serialVersionUID = -8630585517417637381L;

	// Variaveis da estrutura
	
	/** O número da transição */ 
	public final int numero;
	
	public Etapa[] etapasAnteriores = null;
	public Etapa[] etapasPosteriores = null;
	transient Method receptividade = null;
	
	// Variaveis de simulacao 
	transient boolean valida;
	transient boolean disparar;
	
	/**
	 * Construtor
	 * @param numero
	 */
	Transicao(int numero) {
		this.numero = numero;		
	}
			 
	/*  @see java.lang.Object#hashCode() */
	public int hashCode() { return numero; }

	/* @see java.lang.Comparable#compareTo(java.lang.Object) */
	public int compareTo(Object arg0) {
		return ((Transicao)arg0).numero - numero;
	}
	
	/*  @see java.lang.Object#toString() */
	public String toString() {
		String s = "Transicao: " + numero + " Valida: " + valida;
		
		s += ", Etapas Anteriores:";			
		if (etapasAnteriores != null) 
			for (int i = 0; i < etapasAnteriores.length; i++)
				s += " " + etapasAnteriores[i].numero;
				
		s += ", Etapas Posteriores:";			
		if (etapasPosteriores != null) 
			for (int i = 0; i < etapasPosteriores.length; i++)
				s += " " + etapasPosteriores[i].numero;			
		if (receptividade != null) 
			s += ", Receptividade: \"" + receptividade.getName()+"\"";				 
		return s;
	}		
	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Transicao))
			return false;
		Transicao other = (Transicao) obj;
		return this.numero == other.numero 
			&& Arrays.equals(this.etapasAnteriores, other.etapasAnteriores)
			&& Arrays.equals(this.etapasPosteriores, other.etapasPosteriores);
	}
}