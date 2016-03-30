package de.fhwedel.om.masks;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import de.fhwedel.om.model.Kreditvertrag;
import de.fhwedel.om.model.Kreditvertrag.Kreditvertragsstatus;
import de.fhwedel.om.model.Zahlung;
import de.fhwedel.om.model.Zahlung.Zahlungstyp;
import de.fhwedel.om.widgets.BOSelectListBox;
import de.fhwedel.om.widgets.EnumSelectListBox;

public class KreditvertragMask extends BusinessMask<Kreditvertrag> implements
		Editor<Kreditvertrag> {

	// Order-Editor
	interface KreditvertragEditorDriver extends
			SimpleBeanEditorDriver<Kreditvertrag, KreditvertragMask> {
	}

	private final KreditvertragEditorDriver editorDriver = GWT
			.create(KreditvertragEditorDriver.class);

	// UiBinder
	interface KreditvertragMaskUiBinder extends
			UiBinder<Widget, KreditvertragMask> {
	}

	private final static KreditvertragMaskUiBinder uiBinder = GWT
			.create(KreditvertragMaskUiBinder.class);

	// UiBinder (Widget-Instanzvariablen)
	@UiField
	TextBox vertragsnummer;
	@UiField
	EnumSelectListBox<Kreditvertragsstatus> status;
	@Ignore
	@UiField
	BOSelectListBox<Zahlung, Integer> zahlung;
	@Ignore
	@UiField
	BOSelectListBox<Kreditvertrag, Integer> kreditvertrag_searchbox;

	@UiField
	CaptionPanel kreditnehmer;

	@UiField
	DateBox erfassungsdatum;
	@UiField
	IntegerBox laufzeit_monate;
	@UiField
	IntegerBox kreditsumme;
	@UiField
	DateBox vertragsbeginn;
	@UiField
	IntegerBox tilgungsrate;
	@UiField
	IntegerBox zinssatz;
	@UiField
	TextBox iban;
	@UiField
	TextBox bic;
	@Ignore
	@UiField
	IntegerBox tilgungssumme;
	@Ignore
	@UiField
	IntegerBox zinssumme;
	@Ignore
	@UiField
	IntegerBox restschuld;
	@Ignore
	@UiField
	IntegerBox ubrigevertragsmonate;
	@UiField
	Button search_kreditvertrag;
	@UiField
	Button load_kreditvertrag;
	@UiField
	Button save_kreditvertrag;
	@UiField
	Button oneRate;
	@UiField
	Button allRate;
	@UiField
	Button edit_kreditnehmer;
	@UiField
	Button create_zahlung;
	@UiField
	Button edit_zahlung;
	@UiField
	Button takeRestschuld;
	@UiField
	Button askforRestschuld;
	@Ignore
	@UiField
	TextBox search_kreditvertragnr;
	
	//Stati
	@UiField
	Button status_ausfertigen;
	@UiField
	Button status_ausgezahlt;
	@UiField
	Button status_widerruf;
	@UiField
	Button status_abgeschlossen;
	@UiField
	Button status_abgelehnt_Bonitaet;
	@UiField
	Button status_abgelehnt_Fristablauf;

	public KreditvertragMask(Kreditvertrag o) {
		initWidget(uiBinder.createAndBindUi(this));
		this.editorDriver.initialize(this);
		this.refreshStatus();
		refreshKreditvertrag();
		this.setBO(o);
	}

	public void setBO(Kreditvertrag o) {
		super.setBO(o);
		Window.alert("setBo");
		this.kreditnehmer.clear();
		this.kreditnehmer.add(new KreditnehmerMask(this.getBO()
				.getKreditnehmer(), true));
		this.create_zahlung.setVisible(this.getBO().getID() != null);
		this.edit_zahlung.setVisible(this.zahlung.getValue() != null);
		this.refreshZahlung();
		refreshKreditvertrag();
		refreshFields();
		this.vertragsnummer.setReadOnly(true);
		this.tilgungssumme.setReadOnly(true);
		this.zinssumme.setReadOnly(true);
		this.restschuld.setReadOnly(true);
		this.ubrigevertragsmonate.setReadOnly(true);
		this.editorDriver.edit(o);
		setVisibilities();
		//setZinsSatz();
	}

	@Override
	protected void saveBO() {
		Window.alert("saveBO");
		if (this.zinssatz.getValue() < 100) {
			this.zinssatz.setValue(this.zinssatz.getValue() * 100);
		}
		if (this.kreditsumme.getValue() > this.getBO().getKreditnehmer()
				.getSelbstauskunft().getMaximale_kreditsumme()) {
			Window.alert("Maximale Kreditsumme zu Hoch. Information an Kunden verschickt.");
		} else {
			this.editorDriver.flush();
			this.getService().save(this.getBO(),
					new AsyncCallback<Kreditvertrag>() {
						@Override
						public void onSuccess(Kreditvertrag result) {
							if (result.getZinssatz() != 0) {
								KreditvertragMask.this.setBO(result);
								KreditvertragMask.this.fireSaved();
								refreshKreditvertrag();
								refreshZahlung();
								refreshFields();
								Window.alert("Kreditvertrag gespeichert, Daten an Kunden geschickt.");
							} else {
								Window.alert("Keine passende Kondition gefunden!");
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							if (caught instanceof NullPointerException) {
								Window.alert(caught.getMessage());
							}
							Window.alert("Fehler beim Speichern ("
									+ KreditvertragMask.this.getBO().getClass()
											.getSimpleName() + ").");
						}
					});
		}
	}

	protected void refreshKreditvertrag() {
		this.getService().getAllKreditvertrage(
				new AsyncCallback<List<Kreditvertrag>>() {
					@Override
					public void onSuccess(List<Kreditvertrag> result) {
						kreditvertrag_searchbox.setAcceptableValues(result);
						refreshZahlung();
						refreshStatus();
						refreshFields();
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim Laden der Kunden.");
					}
				});
	}

	protected void refreshStatus() {
		this.status.setEnum(Kreditvertragsstatus.class);
	}

	protected void refreshFields() {
		int zinssum = 0;
		int rest = 0;
		int monat = 0;
		int tilg = 0;

		// Durch das Teilen steht da immer ein Satz von 0 Prozent
		// ist das so geplant?
		this.zinssatz.setValue(this.getBO().getZinssatz() / 100);
		if (this.getBO().getZahlung() != null) {
			zinssum = this.getBO().getKreditsumme()
					* this.getBO().getZinssatz();
			zinssum = zinssum / 10000;
			this.zinssumme.setValue(zinssum);
			rest = this.getBO().getKreditsumme() + zinssum;
			for (int i = 0; i < this.getBO().getZahlung().size(); i++) {
				if (this.getBO().getZahlung().get(i).getTyp() == Zahlungstyp.Rate
						|| this.getBO().getZahlung().get(i).getTyp() == Zahlungstyp.Sondertilgung) {
					tilg = tilg
							+ this.getBO().getZahlung().get(i)
									.getZahlungsbetrag();
				}
			}
			this.tilgungssumme.setValue(tilg);
			for (int i = 0; i < this.getBO().getZahlung().size(); i++) {
				if (this.getBO().getZahlung().get(i).getTyp() == Zahlungstyp.Rate) {
					monat = monat + 1;
				}
			}
			monat = this.getBO().getLaufzeit_monate() - monat;
			this.ubrigevertragsmonate.setValue(monat);
			rest = this.getBO().getKreditsumme() - tilg;
			this.restschuld.setValue(rest);
		}
	}

	protected void refreshZahlung() {
		if (this.getBO().getZahlung() != null)
			zahlung.setAcceptableValues(this.getBO().getZahlung());
		else
			zahlung.clear();
	}

	@UiHandler("search_kreditvertrag")
	protected void onSearchOrderClick(ClickEvent event) {
		this.getService().getAllKreditvertrage(
				new AsyncCallback<List<Kreditvertrag>>() {
					@Override
					public void onSuccess(List<Kreditvertrag> result) {
						List<Kreditvertrag> searchList = new LinkedList<Kreditvertrag>();
						for (int i = 0; i < result.size(); i++) {
							if (search_kreditvertragnr.getValue() != "") {
								if (search_kreditvertragnr.getValue() == result
										.get(i).getVertragsnummer()) {
									searchList.add(result.get(i));
								}
							}
							if (search_kreditvertragnr.getValue() != "") {
								kreditvertrag_searchbox
										.setAcceptableValues(searchList);
							} else {
								kreditvertrag_searchbox
										.setAcceptableValues(result);
							}
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim Laden der Kunden.");
					}
				});
	}

	@UiHandler("save_kreditvertrag")
	protected void onSaveOrderClick(ClickEvent event) {
		this.saveBO();
	}

	@UiHandler("askforRestschuld")
	protected void onaskforRestschuldClick(ClickEvent event) {
		Window.alert("Aufforderung an Kunden versendet.");
	}

	@UiHandler("load_kreditvertrag")
	protected void onSelectCustomerClick(ClickEvent event) {
		this.setBO(this.kreditvertrag_searchbox.getValue());
	}

	@UiHandler("kreditvertrag_searchbox")
	protected void onSelectCustomerClick(DoubleClickEvent event) {
		this.setBO(this.kreditvertrag_searchbox.getValue());
	}

	@UiHandler("zahlung")
	protected void onSelectOrderPositionClick(ClickEvent event) {
		this.edit_zahlung.setVisible(this.zahlung.getValue() != null);
	}

	@UiHandler("edit_kreditnehmer")
	protected void onEditCustomerClick(ClickEvent event) {
		this.getFlowControl().forward(
				new KreditnehmerMask(this.getBO().getKreditnehmer()));
	}

	@UiHandler("create_zahlung")
	protected void onNewOrderPositionClick(ClickEvent event) {
		Zahlung new_pos = new Zahlung();
		new_pos.setKreditvertrag(this.getBO());
		this.editOrderPosition(new_pos);
	}

	@UiHandler("zahlung")
	protected void onEditOrderPositionClick(DoubleClickEvent event) {
		this.editOrderPosition(this.zahlung.getValue());
	}

	@UiHandler("edit_zahlung")
	protected void onEditOrderPositionClick(ClickEvent event) {
		this.editOrderPosition(this.zahlung.getValue());
	}

	protected void takeRate() {
		Zahlung new_pos = new Zahlung(this.getBO(), null,
				(Math.min(this.getBO().getTilgungsrate(),
						this.restschuld.getValue()) + (this.restschuld
						.getValue() * this.getBO().getZinssatz() / 120000)),
				Zahlungstyp.Rate);
		this.getBO().getZahlung().add(new_pos);
		this.setBO(this.getBO());
	}

	@UiHandler("takeRestschuld")
	protected void onTakeRestschuldClick(ClickEvent event) {
		Zahlung new_pos = new Zahlung(this.getBO(), null,
				this.restschuld.getValue(), Zahlungstyp.Ablosung);
		this.getBO().getZahlung().add(new_pos);
		this.status.setSelectedIndex(6);
		this.restschuld.setValue(0);
		this.setBO(this.getBO());
	}

	@UiHandler("oneRate")
	protected void onOneRateClick(ClickEvent event) {
		takeRate();
	}

	@UiHandler("allRate")
	protected void onAllRateClick(ClickEvent event) {
		while (this.restschuld.getValue() != 0
				|| this.ubrigevertragsmonate.getValue() != 0) {
			takeRate();
		}
	}

	protected void editOrderPosition(final Zahlung pos) {
		final ZahlungMask opm = new ZahlungMask(pos);
		final DialogBox dialog = new DialogBox();
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		dialog.setModal(true);
		dialog.setText("Zahlung bearbeiten");

		MaskListener ml = new MaskListener() {
			@Override
			public void completed() {
				KreditvertragMask.this.setBO(opm.getBO().getKreditvertrag());
			}

			@Override
			public void cancelled() {
				dialog.hide();
			}
		};

		opm.addMaskListener(ml);
		dialog.setWidget(opm);
		dialog.setPixelSize(800, 400);
		dialog.center();
		dialog.show();
	}
	
	// Stati
	@UiHandler("status_ausfertigen")
	protected void onstatus_ausfertigenClick(ClickEvent event) {
		if (this.isCreditValid()) {
			this.status.setSelectedIndex(3);
			this.saveBO();
			Window.alert("Kredit ausgefertigt.");
		}
		else {
			Window.alert("Bitte Angaben überprüfen.");
		}
		setVisibilities();
	}
	
	@UiHandler("status_ausgezahlt")
	protected void onstatus_ausgezahltClick(ClickEvent event) {
		   this.status.setSelectedIndex(4);
		   this.saveBO();
		   Window.alert("Ueberweisung der Kreditsumme an Kundenbank.");
		   Zahlung new_payment = new Zahlung();
		   new_payment.setKreditvertrag(this.getBO()); 
		   new_payment.setZahlungsbetrag(this.getBO().getKreditsumme());
		   new_payment.setTyp(Zahlungstyp.Auszahlung);
		   new_payment.setZahlungsdatum(new Date(System.currentTimeMillis()));
		   this.getFlowControl().forward( new ZahlungMask(new_payment) );
		   setVisibilities();
	}
	
	@UiHandler("status_widerruf")
	protected void onstatus_widerrufClick(ClickEvent event) {
        this.status.setSelectedIndex(5);
        this.saveBO();
        Window.alert("Kredit widerrufen");
        setVisibilities();
	}
	
	@UiHandler("status_abgeschlossen")
	protected void onstatus_abgeschlossenClick(ClickEvent event) {
		if (this.restschuld.getValue() == 0){
			this.status.setSelectedIndex(6);
			this.saveBO();
			Window.alert("Kunde über Vertragsende informieren");
		} else {
			this.takeRestschuld.setVisible(true);
			Window.alert("Kunde auffordern Restschuld zu überweisen");
		}
		setVisibilities();
	}
	
	@UiHandler("status_abgelehnt_Bonitaet")
	protected void onStatus_abgelehnt_BonitaetClick(ClickEvent event) {
		if (this.getBO().getKreditnehmer().getSelbstauskunft().getBonitatsindex() >= 100) {
		  Window.alert("Absage wegen Bonitaet an Kunden versenden.");}
		else {
		  Window.alert("Bonitaet des Kunden ausreichend.");	
		}
		setVisibilities();
	}
	
	@UiHandler("status_abgelehnt_Fristablauf")
	protected void onstatus_abgelehnt_FristablaufClick(ClickEvent event) {
		   this.status.setSelectedIndex(2);
		   this.saveBO();
		   Window.alert("Absage wegen Fristablauf an Kunden versenden."); 
		   setVisibilities();
	}
	
	private boolean isCreditValid(){
		boolean result;
		result = this.laufzeit_monate.getValue() != 0;
		result = result && this.kreditsumme.getValue() != 0;
		result = result && this.tilgungsrate.getValue() !=0;
		result = result && this.bic.getValue() != "";
		result = result && this.iban.getValue() != "";
		return result;
	}
    
	private void setVisibilities(){
		switch(this.getBO().getStatus().toString()){
		case "Angebot":
			this.status_abgelehnt_Bonitaet.setVisible(true);
			this.status_abgelehnt_Fristablauf.setVisible(true);
			this.status_abgeschlossen.setVisible(false);
			this.status_ausfertigen.setVisible(true);
			this.status_ausgezahlt.setVisible(false);
			this.status_widerruf.setVisible(false);
			break;
		case "Abgelehnt wegen Bonitaet":
		case "Abgelehnt wegen Fristablauf":
			this.status_abgelehnt_Bonitaet.setVisible(false);
			this.status_abgelehnt_Fristablauf.setVisible(false);
			this.status_abgeschlossen.setVisible(false);
			this.status_ausfertigen.setVisible(false);
			this.status_ausgezahlt.setVisible(false);
			this.status_widerruf.setVisible(false);
			break;
		case "Ausgefertigt":
			this.status_abgelehnt_Bonitaet.setVisible(false);
			this.status_abgelehnt_Fristablauf.setVisible(false);
			this.status_abgeschlossen.setVisible(false);
			this.status_ausfertigen.setVisible(false);
			this.status_ausgezahlt.setVisible(true);
			this.status_widerruf.setVisible(true);
			break;
		case "Ausgezahlt":
			this.status_abgelehnt_Bonitaet.setVisible(false);
			this.status_abgelehnt_Fristablauf.setVisible(false);
			this.status_abgeschlossen.setVisible(true);
			this.status_ausfertigen.setVisible(false);
			this.status_ausgezahlt.setVisible(false);
			this.status_widerruf.setVisible(false);
			break;
		case "Widerruf":
		case "Abgeschlossen":
			this.status_abgelehnt_Bonitaet.setVisible(false);
			this.status_abgelehnt_Fristablauf.setVisible(false);
			this.status_abgeschlossen.setVisible(false);
			this.status_ausfertigen.setVisible(false);
			this.status_ausgezahlt.setVisible(false);
			this.status_widerruf.setVisible(false);
			break;
		}
	}
	
	private void setZinsSatz(){
		Integer bon_index = this.getBO().getKreditnehmer().getSelbstauskunft().getBonitatsindex();
		Window.alert(bon_index.toString());
		if (isBetween(bon_index, 100, 200)){this.getBO().setZinssatz(1); zinssatz.setValue(1);}
		else if (isBetween(bon_index, 200, 300)){this.getBO().setZinssatz(2);zinssatz.setValue(2);}
		else if (isBetween(bon_index, 300, 400)){this.getBO().setZinssatz(3);zinssatz.setValue(3);}
		else if (isBetween(bon_index, 400, 500)){this.getBO().setZinssatz(4);zinssatz.setValue(4);}
		else if (isBetween(bon_index, 500, 600)){this.getBO().setZinssatz(5);zinssatz.setValue(5);}
		else {this.getBO().setZinssatz(1);zinssatz.setValue(1);}		
	}
	
	public static boolean isBetween(int x, int lower, int upper) {
		  return lower <= x && x <= upper;
		}

}
