package de.fhwedel.om.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@NamedQueries({ @NamedQuery(name = "getAllZahlungen", query = "SELECT o FROM Zahlung o") })
public class Zahlung implements BusinessObject<Integer> {

	public enum Zahlungstyp {
		Auszahlung("Auszahlung"), Rate("Rate"), Sondertilgung("Sondertilgung"), Ablosung(
				"Ablosung");

		private final String caption;

		private Zahlungstyp(String caption) {
			this.caption = caption;
		};

		public String toString() {
			return this.caption;
		}
	};

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	@Column(name = "zahlung_id")
	private Integer zahlung_id;

	@Enumerated(EnumType.STRING)
	private Zahlungstyp typ;

	@Temporal(TemporalType.DATE)
	private Date zahlungsdatum;

	private int zahlungsbetrag;

	@ManyToOne(cascade = { CascadeType.REFRESH })
	private Kreditvertrag kreditvertrag;

	public Zahlung() {
		this(null, null, 0, Zahlungstyp.Auszahlung);
	}

	public Zahlung(Kreditvertrag kreditvertrag, Date zahlungsdatum,
			int zahlungsbetrag, Zahlungstyp typ) {
		this.kreditvertrag = kreditvertrag;
		this.zahlungsdatum = zahlungsdatum;
		this.zahlungsbetrag = zahlungsbetrag;
		this.typ = typ;
	}

	public Zahlungstyp getTyp() {
		return typ;
	}

	public void setTyp(Zahlungstyp typ) {
		this.typ = typ;
	}

	public Date getZahlungsdatum() {
		return zahlungsdatum;
	}

	public void setZahlungsdatum(Date zahlungsdatum) {
		this.zahlungsdatum = zahlungsdatum;
	}

	public int getZahlungsbetrag() {
		return zahlungsbetrag;
	}

	public void setZahlungsbetrag(int zahlungsbetrag) {
		this.zahlungsbetrag = zahlungsbetrag;
	}

	public Kreditvertrag getKreditvertrag() {
		return kreditvertrag;
	}

	public void setKreditvertrag(Kreditvertrag kreditvertrag) {
		this.kreditvertrag = kreditvertrag;
	}

	@Override
	public String toString() {
		return this.getCaption();
	}

	// BusinessObject-Methoden

	@Override
	public Integer getID() {
		return this.zahlung_id;
	}

	@Override
	public String getCaption() {
		return "Zahlung Nr. " + this.getID().toString(); // "[Auftrag " + this.getOrderCaption() + "]";
	}

}