package de.fhwedel.om.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@NamedQueries({ @NamedQuery(name = "getAllKondition", query = "SELECT o FROM Kondition o") })
public class Kondition implements BusinessObject<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	@Column(name = "kondition_id")
	private Integer kondition_id;

	private int laufzeit_monate_von;

	private int laufzeit_monate_bis;

	private int kreditsumme_von;

	private int kreditsumme_bis;

	private int bonitatsindex_von;

	private int bonitatsindex_bis;

	@Temporal(TemporalType.DATE)
	private Date gultig_von;
	
	@Temporal(TemporalType.DATE)
	private Date gultig_bis;

	private int zinssatz;

	public Kondition() {
		this(0, 0, 0, 0, 100, 100, null, null, 0);
	}

	public Kondition(int laufzeit_monate_von, int laufzeit_monate_bis,
			int kreditsumme_von, int kreditsumme_bis, int bonitatsindex_von,
			int bonitatsindex_bis, Date gultig_von, Date gultig_bis, int zinssatz) {
		this.laufzeit_monate_von = laufzeit_monate_von;
		this.laufzeit_monate_bis = laufzeit_monate_bis;
		this.kreditsumme_von = kreditsumme_von;
		this.kreditsumme_bis = kreditsumme_bis;
		this.bonitatsindex_von = bonitatsindex_von;
		this.bonitatsindex_bis = bonitatsindex_bis;
		this.gultig_von = gultig_von;
		this.gultig_bis = gultig_bis;
		this.zinssatz = zinssatz;
	}

	public int getLaufzeit_monate_von() {
		return laufzeit_monate_von;
	}

	public void setLaufzeit_monate_von(int laufzeit_monate_von) {
		this.laufzeit_monate_von = laufzeit_monate_von;
	}

	public int getLaufzeit_monate_bis() {
		return laufzeit_monate_bis;
	}

	public void setLaufzeit_monate_bis(int laufzeit_monate_bis) {
		this.laufzeit_monate_bis = laufzeit_monate_bis;
	}

	public int getKreditsumme_von() {
		return kreditsumme_von;
	}

	public void setKreditsumme_von(int kreditsumme_von) {
		this.kreditsumme_von = kreditsumme_von;
	}

	public int getKreditsumme_bis() {
		return kreditsumme_bis;
	}

	public void setKreditsumme_bis(int kreditsumme_bis) {
		this.kreditsumme_bis = kreditsumme_bis;
	}

	public int getBonitatsindex_von() {
		return bonitatsindex_von;
	}

	public void setBonitatsindex_von(int bonitatsindex_von) {
		this.bonitatsindex_von = bonitatsindex_von;
	}

	public int getBonitatsindex_bis() {
		return bonitatsindex_bis;
	}

	public void setBonitatsindex_bis(int bonitatsindex_bis) {
		this.bonitatsindex_bis = bonitatsindex_bis;
	}

	public Date getGultig_von() {
		return gultig_von;
	}

	public void setGultig_von(Date gultig_von) {
		this.gultig_von = gultig_von;
	}

	public int getZinssatz() {
		return zinssatz;
	}

	public void setZinssatz(int zinssatz) {
		this.zinssatz = zinssatz;
	}
	
	public boolean isPassend(Kreditvertrag kreditvertrag) {
		
		
		return true;
	}

	// Object-Methoden

	@Override
	public String toString() {
		return this.getCaption();
	}

	// BusinessObject-Methoden

	@Override
	public Integer getID() {
		return this.kondition_id;
	}

	@Override
	public String getCaption() {
		return "Kodition"; // this.getName() + " (EUR " + (this.getPrice() /
							// 100) + "." + (this.getPrice() % 100) + ")";
	}
}