package de.fhwedel.om.masks;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

import de.fhwedel.om.model.BusinessObject;
import de.fhwedel.om.services.OMService;
import de.fhwedel.om.services.OMServiceAsync;

public abstract class BusinessMask<TYPE extends BusinessObject<? extends Serializable>> extends Composite {
   
   private TYPE bo;
   private List<MaskListener> mls = new LinkedList<MaskListener>();
   private FlowControl fc;
  
   // Serverdienste 
   private final OMServiceAsync service = GWT.create(OMService.class);  
    
   public void addMaskListener(MaskListener ml) {
      this.mls.add(ml);
   }
   
   public void removeMaskListener(MaskListener ml) {
      this.mls.remove(ml);
   }
   
   protected void fireSaved() {
      for(MaskListener ml : this.mls) {
         ml.completed();
      }
   }
   
   protected void fireCancelled() {
      for(MaskListener ml : this.mls) {
         ml.cancelled();
      }
   }

   public void setFlowControl(FlowControl fc) {
      this.fc = fc;
   }   
   
   protected FlowControl getFlowControl() {
      return this.fc;
   } 
   
   protected OMServiceAsync getService() {
      return this.service;
   } 

   public TYPE getBO() {
      return this.bo;
   }
   
   public void setBO(TYPE bo) {
      this.bo = bo;
      if(this.getFlowControl() != null)
         this.getFlowControl().update(this);
   }
   
   protected void saveBO() {
      this.getService().save(this.getBO(), new AsyncCallback<TYPE>() {         
         @Override
         public void onSuccess(TYPE result) {
            BusinessMask.this.setBO(result);
            BusinessMask.this.fireSaved();            
         }         
         @Override
         public void onFailure(Throwable caught) {
            Window.alert("Fehler beim Speichern (" + BusinessMask.this.getBO().getClass().getSimpleName() + ").");        
         }
      });
   }
   
   public void refresh() {
      if(this.getBO().getID() != null) {
         this.getService().get(this.getBO().getClass().getName(), this.getBO().getID(), new AsyncCallback<TYPE>() {         
            @Override
            public void onSuccess(TYPE result) {
               BusinessMask.this.setBO(result);            
            }         
            @Override
            public void onFailure(Throwable caught) {
               Window.alert("Fehler beim Neuladen (" + BusinessMask.this.getBO().getClass().getSimpleName() + ").");
            }
         });
      }      
   }

}
