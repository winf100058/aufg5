package de.fhwedel.om.masks;

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

	public KreditvertragMask(Kreditvertrag o) {
		initWidget(uiBinder.createAndBindUi(this));
		this.editorDriver.initialize(this);
		this.refreshStatus();
		refreshKreditvertrag();
		this.setBO(o);
	}

	public void setBO(Kreditvertrag o) {
		super.setBO(o);
		this.kreditnehmer.clear();
		this.kreditnehmer.add(new KreditnehmerMask(this.getBO()
				.getKreditnehmer(), true));
		this.create_zahlung.setVisible(this.getBO().getID() != null);
		this.edit_zahlung.setVisible(this.zahlung.getValue() != null);
		this.refreshZahlung();
		refreshKreditvertrag();
		refreshFields();
		this.vertragsnummer.setReadOnly(true);
		this.editorDriver.edit(o);
	}

	@Override
	protected void saveBO() {
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

}
