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
//import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import de.fhwedel.om.model.Zahlung;
import de.fhwedel.om.model.Zahlung.Zahlungstyp;
import de.fhwedel.om.widgets.EnumSelectListBox;

public class ZahlungMask extends BusinessMask<Zahlung> implements Editor<Zahlung> {
   
   // OrderPosition-Editor
   interface ZahlungEditorDriver extends SimpleBeanEditorDriver<Zahlung, ZahlungMask> {}
   private final ZahlungEditorDriver editorDriver = GWT.create(ZahlungEditorDriver.class);
    
   // UiBinder    
   interface OrderPositionMaskUiBinder extends UiBinder<Widget, ZahlungMask> {}
   private final static OrderPositionMaskUiBinder uiBinder = GWT.create(OrderPositionMaskUiBinder.class);    
    
   // UiBinder (Widget-Instanzvariablen) 
   //@UiField CaptionPanel kreditvertrag;

   
   @UiField EnumSelectListBox<Zahlungstyp> typ;
   @UiField DateBox zahlungsdatum;
   @UiField IntegerBox zahlungsbetrag;
    
   @UiField Button save_zahlung;
   @UiField Button cancel;
       
   public ZahlungMask(Zahlung pos) {
      initWidget(uiBinder.createAndBindUi(this));
      this.editorDriver.initialize(this);
      this.refreshStatus();
      this.setBO(pos);
   }
    
   public void setBO(Zahlung pos) {
      super.setBO(pos);
      this.editorDriver.edit(pos);
   }

   @Override
   protected void saveBO() {
      this.editorDriver.flush();
      this.getService().save(this.getBO(), new AsyncCallback<Zahlung>() {         
         @Override
         public void onSuccess(Zahlung result) {
            ZahlungMask.this.setBO(result);
            ZahlungMask.this.fireSaved();            
         }         
         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Fehler beim Speichern (" + ZahlungMask.this.getBO().getClass().getSimpleName() + ").");        
         }
      });
   }   
   
    
   @UiHandler("save_zahlung")
   protected void saveOrderPosition(ClickEvent e) {      
         this.saveBO();
   }
    
   @UiHandler("cancel")
   protected void cancel(ClickEvent e) {
      this.fireCancelled();
   }
   
   protected void refreshStatus() {
	      this.typ.setEnum(Zahlungstyp.class);     
   }
   
}
