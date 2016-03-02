package de.fhwedel.om.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name="kreditnehmer", uniqueConstraints=@UniqueConstraint(columnNames={"kundennr"}))
@NamedQueries({ @NamedQuery(name = "getAllKreditnehmer", query = "SELECT c FROM Kreditnehmer c") })
public class Kreditnehmer implements BusinessObject<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	@Column(name = "kreditnehmer_id")
	private Integer kreditnehmer_id;

	private String kundennr;

	private String vorname;

	private String name;

	private String strasse;

	private String plz;

	private String ort;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "kreditnehmer", cascade = {
			CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE,
			CascadeType.REFRESH }, orphanRemoval = true)
	private List<Kreditvertrag> kreditvertrage;

	@OneToOne(mappedBy = "kreditnehmer", cascade = { CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH }, orphanRemoval = true)
	private Selbstauskunft selbstauskunft;

	public Kreditnehmer() {
		this("<neu>", "<neu>", "<neu>", "<neu>", "<neu>", "<neu>");
	}

	public Kreditnehmer(String kundennr, String vorname, String name,
			String strasse, String plz, String ort) {
		this.kundennr = kundennr;
		this.vorname = vorname;
		this.name = name;
		this.strasse = strasse;
		this.plz = plz;
		this.ort = ort;
		this.kreditvertrage = new LinkedList<Kreditvertrag>();
		//this.selbstauskunft = new Selbstauskunft();
	}

	public String getKundennr() {
		return this.kundennr;
	}

	public void setKundennr(String kundennr) {
		this.kundennr = kundennr;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public List<Kreditvertrag> getKreditvertrage() {
		return this.kreditvertrage;
	}

	public void setKreditvertrage(List<Kreditvertrag> kreditvertrage) {
		this.kreditvertrage = kreditvertrage;
	}

	public Selbstauskunft getSelbstauskunft() {
		return this.selbstauskunft;
	}

	public void setSelbstauskunft(Selbstauskunft selbstauskunft) {
		this.selbstauskunft = selbstauskunft;
	}
	
	public void calcKundNr(int index) {
		this.setKundennr("K" + "2016" + ((Integer)index).toString());
	}

	// Object-Methoden

	@Override
	public String toString() {
		return this.getCaption();
	}

	// BusinessObject-Methoden

	@Override
	public Integer getID() {
		return this.kreditnehmer_id;
	}

	// TODO: Ggf. mehr infos in der Caption
	@Override
	public String getCaption() {
		return "Kreditnehmer " + this.getKundennr(); 
	}

}