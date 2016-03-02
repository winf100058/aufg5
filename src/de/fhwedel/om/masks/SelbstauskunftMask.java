package de.fhwedel.om.masks;


import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import de.fhwedel.om.model.Selbstauskunft;
import de.fhwedel.om.model.Selbstauskunft.Beschaftigungsarten;
import de.fhwedel.om.widgets.EnumSelectListBox;

public class SelbstauskunftMask extends BusinessMask<Selbstauskunft> implements Editor<Selbstauskunft> {
   
   // OrderPosition-Editor
   interface SelbstauskunftEditorDriver extends SimpleBeanEditorDriver<Selbstauskunft, SelbstauskunftMask> {}
   private final SelbstauskunftEditorDriver editorDriver = GWT.create(SelbstauskunftEditorDriver.class);
    
   // UiBinder    
   interface OrderPositionMaskUiBinder extends UiBinder<Widget, SelbstauskunftMask> {}
   private final static OrderPositionMaskUiBinder uiBinder = GWT.create(OrderPositionMaskUiBinder.class);    
    
   // UiBinder (Widget-Instanzvariablen) 
   @UiField DateBox erfassungsdatum;
   @UiField TextBox beruf;
   @UiField EnumSelectListBox<Beschaftigungsarten> beschaftigungsart;
   @UiField CheckBox befristet;
   @UiField DateBox beschaftigt_seit;
   @UiField IntegerBox jahresnetto;
   @UiField DateBox einstufungsdatum;
   @UiField IntegerBox bonitatsindex;
   @UiField IntegerBox maximale_kreditsumme;
   
   @UiField CaptionPanel kreditnehmer;
   
   @UiField Button save_selbstauskunft;
   @UiField Button cancel;
       
   public SelbstauskunftMask(Selbstauskunft pos) {
      initWidget(uiBinder.createAndBindUi(this));
      this.editorDriver.initialize(this);
      this.bonitatsindex.setReadOnly(true);
      this.maximale_kreditsumme.setReadOnly(true);
      this.refreshStatus();
      this.setBO(pos);
   }
    
   public void setBO(Selbstauskunft pos) {
      super.setBO(pos);
      this.kreditnehmer.clear();
      this.kreditnehmer.add( new KreditnehmerMask(this.getBO().getKreditnehmer(), true) );
      this.editorDriver.edit(pos);
   }
   
   protected void refreshStatus() {
	      this.beschaftigungsart.setEnum(Beschaftigungsarten.class);     
   }

   @Override
   protected void saveBO() {
      this.editorDriver.flush();
      this.getService().save(this.getBO(), new AsyncCallback<Selbstauskunft>() {         
         @Override
         public void onSuccess(Selbstauskunft result) {
            SelbstauskunftMask.this.setBO(result);
            SelbstauskunftMask.this.fireSaved();            
         }         
         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Fehler beim Speichern (" + SelbstauskunftMask.this.getBO().getClass().getSimpleName() + ").");        
         }
      });
   }   
    
   @UiHandler("save_selbstauskunft")
   protected void saveOrderPosition(ClickEvent e) {      
         this.saveBO();
   }
    
   @UiHandler("cancel")
   protected void cancel(ClickEvent e) {
      this.fireCancelled();
   }
   
   
}
