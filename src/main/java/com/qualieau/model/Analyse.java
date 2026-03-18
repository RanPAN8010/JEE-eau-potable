package com.qualieau.model;
import java.time.LocalDate;
/**
 * Représente une analyse de la qualité de l'eau.
 * Cette classe contient les résultats des prélèvements et les indicateurs de conformité.
 * * @author Ran
 * @version 1.0
 */
public class Analyse {
	
	/** Identifiant unique en base de données. */
	private Long id; 
	
	/** Date du prélèvement de l'échantillon. */
    private LocalDate datePrelevement;
    
    /** Libellé du paramètre analysé (ex: Chlore, Plomb). */
    private String parametre; 
    
    /** Résultat chiffré de l'analyse. */
    private Double valeur;  
    
    /** Unité associée à la valeur (ex: µg/L). */
    private String unite;      
    
    /** État de conformité de l'échantillon par rapport aux seuils réglementaires. */
    private boolean conforme;
    
    /** Code INSEE de la commune où le prélèvement a été réalisé. */
    private String codeInsee;
    
    /**
     * getters et setters
     */
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getDatePrelevement() {
		return datePrelevement;
	}
	public void setDatePrelevement(LocalDate datePrelevement) {
		this.datePrelevement = datePrelevement;
	}
	public String getParametre() {
		return parametre;
	}
	public void setParametre(String parametre) {
		this.parametre = parametre;
	}
	public Double getValeur() {
		return valeur;
	}
	public void setValeur(Double valeur) {
		this.valeur = valeur;
	}
	public String getUnite() {
		return unite;
	}
	public void setUnite(String unite) {
		this.unite = unite;
	}
	public boolean isConforme() {
		return conforme;
	}
	public void setConforme(boolean conforme) {
		this.conforme = conforme;
	}
	public String getCodeInsee() {
		return codeInsee;
	}
	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}       


}
