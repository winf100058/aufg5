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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.fhwedel.om.model.Kreditnehmer;
import de.fhwedel.om.model.Kreditvertrag;
import de.fhwedel.om.model.Selbstauskunft;
import de.fhwedel.om.widgets.BOSelectListBox;

public class KreditnehmerMask extends BusinessMask<Kreditnehmer> implements
		Editor<Kreditnehmer> {

	private boolean show_only;

	// Customer-Editor
	interface KreditnehmerEditorDriver extends
			SimpleBeanEditorDriver<Kreditnehmer, KreditnehmerMask> {
	}

	private final KreditnehmerEditorDriver editorDriver = GWT
			.create(KreditnehmerEditorDriver.class);

	// UiBinder
	interface KreditnehmerMaskUiBinder extends
			UiBinder<Widget, KreditnehmerMask> {
	}

	private final static KreditnehmerMaskUiBinder uiBinder = GWT
			.create(KreditnehmerMaskUiBinder.class);

	// UiBinder (Widget-Instanzvariablen)
	@UiField
	TextBox kundennr;
	@UiField
	TextBox name;
	@UiField
	TextBox vorname;
	@UiField
	TextBox strasse;
	@UiField
	TextBox plz;
	@UiField
	TextBox ort;
	@Ignore
	@UiField
	BOSelectListBox<Kreditnehmer, Integer> kreditnehmer_searchbox;
	@Ignore
	@UiField
	BOSelectListBox<Selbstauskunft, Integer> selbstauskunft;
	@Ignore
	@UiField
	BOSelectListBox<Kreditvertrag, Integer> kreditvertrag;

	@UiField
	Button search_kreditnehmer;
	@UiField
	Button load_kreditnehmer;
	@UiField
	Button save_kreditnehmer;
	@UiField
	Button new_kreditnehmer;
	@UiField
	Button create_selbstauskunft;
	@UiField
	Button edit_selbstauskunft;
	@UiField
	Button create_kreditvertrag;
	@UiField
	Button edit_kreditvertrage;
	
	@Ignore @UiField Label name_label;
	@Ignore @UiField Label vorname_label;
	@Ignore @UiField Label kundennr_label;
	@Ignore @UiField TextBox search_name;
	@Ignore @UiField TextBox search_kundennr;
	@Ignore @UiField TextBox search_vorname;
	@Ignore @UiField AbsolutePanel aPanel;
	

	public KreditnehmerMask() {
		this(new Kreditnehmer());
	}

	public KreditnehmerMask(Kreditnehmer c) {
		this(c, false);
	}

	public KreditnehmerMask(Kreditnehmer c, boolean show_only) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setMode(show_only);
		this.editorDriver.initialize(this);
		this.refreshKreditnehmer();
		this.setBO(c);
	}

	protected void setMode(boolean show_only) {
		this.show_only = show_only;
		this.kundennr.setReadOnly(true);
		this.name.setReadOnly(show_only);
		this.vorname.setReadOnly(show_only);
		this.strasse.setReadOnly(show_only);
		this.plz.setReadOnly(show_only);
		this.ort.setReadOnly(show_only);
		this.search_kreditnehmer.setVisible(!show_only);
		this.new_kreditnehmer.setVisible(!show_only);
		this.save_kreditnehmer.setVisible(!show_only);
		this.load_kreditnehmer.setVisible(!show_only);
		this.kreditnehmer_searchbox.setVisible(!show_only);
		this.selbstauskunft.setVisible(!show_only);
		this.kreditvertrag.setVisible(!show_only);
		this.name_label.setVisible(!show_only);
		this.vorname_label.setVisible(!show_only);
		this.kundennr_label.setVisible(!show_only);
		this.search_name.setVisible(!show_only);
		this.search_kundennr.setVisible(!show_only);
		this.search_vorname.setVisible(!show_only);
		if(show_only) {
			//Change aPanel left position??
		}
	}

	public void setBO(Kreditnehmer c) {
		super.setBO(c);
		this.edit_selbstauskunft.setVisible(!this.show_only
				&& this.getBO().getID() != null);
		this.create_selbstauskunft.setVisible(!this.show_only
				&& this.getBO().getID() != null);
		this.create_kreditvertrag.setVisible(!this.show_only
				&& this.getBO().getID() != null);
		this.edit_kreditvertrage.setVisible(!this.show_only
				&& this.getBO().getID() != null);
		this.editorDriver.edit(c);
		this.refreshKreditvertrag();
		this.refreshSelbstauskunft();
	}

	@Override
	protected void saveBO() {
		this.editorDriver.flush();
		this.getService().save(this.getBO(), new AsyncCallback<Kreditnehmer>() {
			@Override
			public void onSuccess(Kreditnehmer result) {
				KreditnehmerMask.this.refreshKreditnehmer();
				KreditnehmerMask.this.refreshSelbstauskunft();
				KreditnehmerMask.this.setBO(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fehler beim Speichern des Kunden.");
			}
		});
	}

	protected void refreshKreditnehmer() {
		this.getService().getAllKreditnehmer(
				new AsyncCallback<List<Kreditnehmer>>() {
					@Override
					public void onSuccess(List<Kreditnehmer> result) {
						kreditnehmer_searchbox.setAcceptableValues(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim Laden der Kunden.");
					}
				});
	}

	private void refreshKreditvertrag() {
		if (this.getBO() != null)
			kreditvertrag.setAcceptableValues(this.getBO().getKreditvertrage());
		else
			kreditvertrag.clear();
		this.edit_kreditvertrage.setVisible(!this.show_only
				&& this.kreditvertrag.getValue() != null);
		this.edit_selbstauskunft.setVisible(!this.show_only
				&& this.selbstauskunft.getValue() != null);
	}

	private void refreshSelbstauskunft() {
		List<Selbstauskunft> selbstauskunftAsList;
		if (this.getBO() != null) {
			selbstauskunftAsList = new LinkedList<Selbstauskunft>();
			selbstauskunftAsList.add(this.getBO().getSelbstauskunft());
			selbstauskunft.setAcceptableValues(selbstauskunftAsList);
		} else {
			selbstauskunft.clear();
		}
		this.edit_selbstauskunft.setVisible(!this.show_only
				&& this.kreditvertrag.getValue() != null);
		this.create_selbstauskunft.setVisible(!this.show_only
				&& this.getBO().getSelbstauskunft() == null);
	}

	@UiHandler("search_kreditnehmer")
	protected void onSearchKreditnehmerClick(ClickEvent event) {
		this.getService().getAllKreditnehmer(
				new AsyncCallback<List<Kreditnehmer>>() {
					@Override
					public void onSuccess(List<Kreditnehmer> result) {
						List<Kreditnehmer> searchList = new LinkedList<Kreditnehmer>();
						for(int i = 0; i < result.size(); i++) {
							if(search_name.getValue() != "") {
								if(search_name.getValue() == result.get(i).getName()) {
									searchList.add(result.get(i));
								}
							}
							if(search_kundennr.getValue() != "") {
								if(search_kundennr.getValue() == result.get(i).getKundennr() ) {
									searchList.add(result.get(i));
								}
							}
							if(search_vorname.getValue() != "") {
								if(search_vorname.getValue() == result.get(i).getVorname()) {
									searchList.add(result.get(i));
								}
							}		
						}
						if((search_name.getValue() == "") && (search_kundennr.getValue() == "") && (search_vorname.getValue() == "")) {
							kreditnehmer_searchbox.setAcceptableValues(result);
						} else {
							kreditnehmer_searchbox.setAcceptableValues(searchList);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Fehler beim Laden der Kunden.");
					}
				});
	}
	
	@UiHandler("save_kreditnehmer")
	protected void onSaveKreditnehmerClick(ClickEvent event) {
		this.saveBO();
	}

	@UiHandler("kreditvertrag")
	protected void onSelectKreditvertragClick(ClickEvent event) {
		this.edit_kreditvertrage.setVisible(!this.show_only
				&& this.kreditvertrag.getValue() != null);
	}
	
	@UiHandler("selbstauskunft")
	protected void onSelectSelbstauskunftClick(ClickEvent event) {
		this.edit_selbstauskunft.setVisible(!this.show_only
				&& this.selbstauskunft.getValue() != null);
	}

	@UiHandler("new_kreditnehmer")
	protected void onNewCustomerClick(ClickEvent event) {
		this.setBO(new Kreditnehmer());
	}

	@UiHandler("load_kreditnehmer")
	protected void onSelectCustomerClick(ClickEvent event) {
		this.setBO(this.kreditnehmer_searchbox.getValue());
	}

	@UiHandler("kreditnehmer_searchbox")
	protected void onSelectCustomerClick(DoubleClickEvent event) {
		this.setBO(this.kreditnehmer_searchbox.getValue());
	}

	@UiHandler("create_kreditvertrag")
	protected void onCreateKreditvertragClick(ClickEvent event) {
		Kreditvertrag new_kreditvertrag = new Kreditvertrag();
		new_kreditvertrag.setKreditnehmer(this.getBO());
		this.getFlowControl().forward(new KreditvertragMask(new_kreditvertrag));
	}
	
	@UiHandler("create_selbstauskunft")
	protected void onCreateselbstauskunftClick(ClickEvent event) {
		Selbstauskunft new_selbstauskunft = new Selbstauskunft();
		new_selbstauskunft.setKreditnehmer(this.getBO());
		this.editSelbstauskunft(new_selbstauskunft);
	}

	@UiHandler("kreditvertrag")
	protected void onSelectKreditvertragClick(DoubleClickEvent event) {
		Kreditvertrag order = this.kreditvertrag.getValue();
		this.getFlowControl().forward(new KreditvertragMask(order));
	}
	
	@UiHandler("selbstauskunft")
	protected void onSelectSelbstauskunftClick(DoubleClickEvent event) {
		this.editSelbstauskunft(this.selbstauskunft.getValue());
	}

	@UiHandler("edit_kreditvertrage")
	protected void onEditKreditvertragClick(ClickEvent event) {
		Kreditvertrag order = this.kreditvertrag.getValue();
		this.getFlowControl().forward(new KreditvertragMask(order));
	}
	
	@UiHandler("edit_selbstauskunft")
	protected void onEditSelbstauskunftClick(ClickEvent event) {
		this.editSelbstauskunft(this.selbstauskunft.getValue());
	}
	
	
	protected void editSelbstauskunft(final Selbstauskunft pos) {
	      final SelbstauskunftMask opm = new SelbstauskunftMask(pos);        
	      final DialogBox dialog = new DialogBox();
	      dialog.setAnimationEnabled(true);
	      dialog.setGlassEnabled(true);
	      dialog.setModal(true);
	      dialog.setText("Selbstauskunft bearbeiten");

	      MaskListener ml = new MaskListener() {
	         @Override
	         public void completed() {            
	            KreditnehmerMask.this.setBO(opm.getBO().getKreditnehmer());
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

	@Override
	public void refresh() {
		this.refreshKreditnehmer();
		super.refresh();
	}

}
