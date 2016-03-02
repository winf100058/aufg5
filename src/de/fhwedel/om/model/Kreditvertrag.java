package de.fhwedel.om.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;


@SuppressWarnings("serial")
@Entity
@Table(name="kreditvertrag", uniqueConstraints=@UniqueConstraint(columnNames={"vertragsnummer"}))
@NamedQueries({ @NamedQuery(name = "getAllKreditvertrage", query = "SELECT o FROM Kreditvertrag o") })
public class Kreditvertrag implements BusinessObject<Integer> {

	public enum Kreditvertragsstatus {

		Angebot("Angebot"), Abgelehnt_wegen_Bonitat("Abgelehnt wegen Bonitat"), Abgelehnt_wegen_Fristablauf(
				"Abgelehnt wegen Fristablauf"), Ausgefertigt("Ausgefertigt"), Ausgezahlt(
				"Ausgezahlt"), Widerruf("Widerruf"), Abgeschlossen(
				"Abgeschlossen");

		private final String caption;

		private Kreditvertragsstatus(String caption) {
			this.caption = caption;
		};

		public String toString() {
			return this.caption;
		}
	};

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	@Column(name = "kreditvertrag_id")
	private Integer kreditvertrag_id;

	@Temporal(TemporalType.DATE)
	private Date erfassungsdatum;

	private String vertragsnummer;

	@Enumerated(EnumType.STRING)
	private Kreditvertragsstatus status;

	private int laufzeit_monate;

	private int kreditsumme;

	@Temporal(TemporalType.DATE)
	private Date vertragsbeginn;

	private int tilgungsrate;

	private int zinssatz;

	private String iban;

	private String bic;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	private Kreditnehmer kreditnehmer;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "kreditvertrag", cascade = {
			CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE,
			CascadeType.REFRESH }, orphanRemoval = true)
	private List<Zahlung> zahlung;

	public Kreditvertrag() {
		this(null, "<neu>", 0, 0, null, 0, 0, "<neu>", "<neu>");
	}

	public Kreditvertrag(Date erfassungsdatum, String vertragsnummer,
			int laufzeit_monate, int kreditsumme, Date vertragsbeginn,
			int tilgungsrate, int zinssatz, String iban, String bic) {
		this.erfassungsdatum = erfassungsdatum;
		this.vertragsnummer = vertragsnummer;
		this.status = Kreditvertragsstatus.Angebot;
		this.laufzeit_monate = laufzeit_monate;
		this.kreditsumme = kreditsumme;
		this.vertragsbeginn = vertragsbeginn;
		this.tilgungsrate = tilgungsrate;
		this.zinssatz = zinssatz;
		this.iban = iban;
		this.bic = bic;
		this.zahlung = new LinkedList<Zahlung>();
	}

	public Date getErfassungsdatum() {
		return erfassungsdatum;
	}

	public void setErfassungsdatum(Date erfassungsdatum) {
		this.erfassungsdatum = erfassungsdatum;
	}

	public String getVertragsnummer() {
		return vertragsnummer;
	}

	public void setVertragsnummer(String vertragsnummer) {
		this.vertragsnummer = vertragsnummer;
	}

	public Kreditvertragsstatus getStatus() {
		return status;
	}

	public void setStatus(Kreditvertragsstatus status) {
		this.status = status;
	}

	public int getLaufzeit_monate() {
		return laufzeit_monate;
	}

	public void setLaufzeit_monate(int laufzeit_monate) {
		this.laufzeit_monate = laufzeit_monate;
	}

	public int getKreditsumme() {
		return kreditsumme;
	}

	public void setKreditsumme(int kreditsumme) {
		this.kreditsumme = kreditsumme;
	}

	public Date getVertragsbeginn() {
		return vertragsbeginn;
	}

	public void setVertragsbeginn(Date vertragsbeginn) {
		this.vertragsbeginn = vertragsbeginn;
	}

	public int getTilgungsrate() {
		return tilgungsrate;
	}

	public void setTilgungsrate(int tilgungsrate) {
		this.tilgungsrate = tilgungsrate;
	}

	public int getZinssatz() {
		return zinssatz;
	}

	public void setZinssatz(int zinssatz) {
		this.zinssatz = zinssatz;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public Kreditnehmer getKreditnehmer() {
		return kreditnehmer;
	}

	public void setKreditnehmer(Kreditnehmer kreditnehmer) {
		this.kreditnehmer = kreditnehmer;
	}

	public List<Zahlung> getZahlung() {
		return zahlung;
	}

	public void setZahlung(List<Zahlung> zahlung) {
		this.zahlung = zahlung;
	}
	
	public void calcVertragnr(int index) {
		this.setVertragsnummer("V" + "2016"  + ((Integer)index).toString());
	}

	// Object-Methoden

	@Override
	public String toString() {
		return this.getCaption();
	}

	// BusinessObject-Methoden

	@Override
	public Integer getID() {
		return this.kreditvertrag_id;
	}

	@Override
	public String getCaption() {
		return "Kreditvertrag: " + this.getVertragsnummer();
	}

}
