package com.qualieau.model;
import java.time.LocalDate;
public class Analyse {
	private Long id;                 
    private LocalDate datePrelevement; 
    private String parametre;         
    private Double valeur;           
    private String unite;            
    private boolean conforme;         
    private String codeInsee;
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
