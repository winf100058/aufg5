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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@NamedQueries({ @NamedQuery(name = "getAllSelbstauskunft", query = "SELECT c FROM Selbstauskunft c") })
public class Selbstauskunft implements BusinessObject<Integer> {

	public enum Beschaftigungsarten {

		Arbeitnehmer("Arbeitnehmer"), Selststandig("Selststandig"), Rentner(
				"Rentner"), Arbeitslos("Arbeitslos");

		private final String caption;

		private Beschaftigungsarten(String caption) {
			this.caption = caption;
		};

		public String toString() {
			return this.caption;
		}
	};

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	@Column(name = "selbstauskunft_id")
	private Integer selbstauskunft_id;

	@Temporal(TemporalType.DATE)
	private Date erfassungsdatum;

	private String beruf;

	@Enumerated(EnumType.STRING)
	private Beschaftigungsarten beschaftigungsart;

	private boolean befristet;

	@Temporal(TemporalType.DATE)
	private Date beschaftigt_seit;

	private int jahresnetto;

	@Temporal(TemporalType.DATE)
	private Date einstufungsdatum;

	private int bonitatsindex;

	private int maximale_kreditsumme;

	@OneToOne(cascade = { CascadeType.REFRESH })
	private Kreditnehmer kreditnehmer;

	public Selbstauskunft() {
		this(null, "<neu>", false, null, 0, null, 0, 0);
	}

	public Selbstauskunft(Date erfassungsdatum, String beruf,
			boolean befristet, Date beschaftigt_seit, int jahresnetto,
			Date einstufungsdatum, int bonitatsindex, int maximale_kreditsumme) {
		this.erfassungsdatum = erfassungsdatum;
		this.beruf = beruf;
		this.befristet = befristet;
		this.beschaftigt_seit = beschaftigt_seit;
		this.jahresnetto = jahresnetto;
		this.einstufungsdatum = einstufungsdatum;
		this.bonitatsindex = bonitatsindex;
		this.maximale_kreditsumme = maximale_kreditsumme;
		this.beschaftigungsart = Beschaftigungsarten.Arbeitnehmer;
	}

	// Object-Methoden

	public Date getErfassungsdatum() {
		return erfassungsdatum;
	}

	public void setErfassungsdatum(Date erfassungsdatum) {
		this.erfassungsdatum = erfassungsdatum;
	}

	public String getBeruf() {
		return beruf;
	}

	public void setBeruf(String beruf) {
		this.beruf = beruf;
	}

	public Beschaftigungsarten getBeschaftigungsart() {
		return beschaftigungsart;
	}

	public void setBeschaftigungsart(Beschaftigungsarten beschaftigungsart) {
		this.beschaftigungsart = beschaftigungsart;
	}

	public boolean isBefristet() {
		return befristet;
	}

	public void setBefristet(boolean befristet) {
		this.befristet = befristet;
	}

	public Date getBeschaftigt_seit() {
		return beschaftigt_seit;
	}

	public void setBeschaftigt_seit(Date beschaftigt_seit) {
		this.beschaftigt_seit = beschaftigt_seit;
	}

	public int getJahresnetto() {
		return jahresnetto;
	}

	public void setJahresnetto(int jahresnetto) {
		this.jahresnetto = jahresnetto;
	}

	public Date getEinstufungsdatum() {
		return einstufungsdatum;
	}

	public void setEinstufungsdatum(Date einstufungsdatum) {
		this.einstufungsdatum = einstufungsdatum;
	}

	public int getBonitatsindex() {
		return bonitatsindex;
	}

	public void setBonitatsindex(int bonitatsindex) {
		this.bonitatsindex = bonitatsindex;
	}

	public int getMaximale_kreditsumme() {
		return maximale_kreditsumme;
	}

	public void setMaximale_kreditsumme(int maximale_kreditsumme) {
		this.maximale_kreditsumme = maximale_kreditsumme;
	}

	public Kreditnehmer getKreditnehmer() {
		return kreditnehmer;
	}

	public void setKreditnehmer(Kreditnehmer kreditnehmer) {
		this.kreditnehmer = kreditnehmer;
	}

	public static boolean isBetween(int x, int lower, int upper) {
		  return lower <= x && x <= upper;
		}
	
	@SuppressWarnings("deprecation")
	public void calcBonitatindex() {
		int besch_index=1;
		int befristet_index=1;
		int seit_index=1;
		int netto_index=1;
		int netto;
		switch (getBeschaftigungsart()) {
		case Arbeitnehmer:
			besch_index = 1;
			break;
		case Arbeitslos:
			besch_index = 6;
			break;
		case Rentner:
			besch_index = 2;
			break;
		case Selststandig:
			besch_index = 3;
			break;
		}
		if (isBefristet()) {
			befristet_index = 6;
		} else {
			befristet_index = 1;
		}

		switch (2016 - getBeschaftigt_seit().getYear()) {
		case 0:
			seit_index = 6;
			break;
		case 1:
			seit_index = 5;
			break;
		case 2:
			seit_index = 4;
			break;
		case 3:
			seit_index = 3;
			break;
		case 4:
			seit_index = 2;
			break;
		default:
			seit_index = 1;
		}
		
		netto=getJahresnetto();
		if (isBetween(netto, 0, 10000)){netto_index=6;}
		else if (isBetween(netto, 10001, 20000)){netto_index=5;}
		else if (isBetween(netto, 20001, 30000)){netto_index=4;}
		else if (isBetween(netto, 30001, 40000)){netto_index=3;}
		else if (isBetween(netto, 40001, 50000)){netto_index=2;}
		else {netto_index=1;}
		
		Double bonitaet=seit_index * 0.25 + befristet_index * 0.25 + besch_index * 0.25 + netto_index + 0.25;
		if(bonitaet.intValue() >= 6) {
			this.setBonitatsindex(600);
		} else {
			setBonitatsindex(bonitaet.intValue() * 100);
		}
	}

	public void calcMaxKreditsum() {
		int bonitaet=getBonitatsindex();
		int kredit=0;
		if (isBetween(bonitaet, 100, 200)){kredit=10000;}
		else if (isBetween(bonitaet, 201, 300)){kredit=8000;}
		else if (isBetween(bonitaet, 301, 400)){kredit=6000;}
		else if (isBetween(bonitaet, 401, 500)){kredit=4000;}
		else if (isBetween(bonitaet, 501, 600)){kredit=2000;}
		this.setMaximale_kreditsumme(kredit);
	}

	@Override
	public String toString() {
		return this.getCaption();
	}

	// BusinessObject-Methoden

	@Override
	public Integer getID() {
		return this.selbstauskunft_id;
	}

	@Override
	public String getCaption() {
		return "Selbstauskunft"; // "[Auftrag " + this.getOrderCaption() + "]";
	}

}
