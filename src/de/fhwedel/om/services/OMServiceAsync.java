package de.fhwedel.om.services;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.fhwedel.om.model.Kondition;
import de.fhwedel.om.model.BusinessObject;
import de.fhwedel.om.model.Kreditnehmer;
import de.fhwedel.om.model.Kreditvertrag;
import de.fhwedel.om.model.Selbstauskunft;
import de.fhwedel.om.model.Zahlung;

public interface OMServiceAsync {
   
   <TYPE extends BusinessObject<?>> void get(String cls, Serializable id, AsyncCallback<TYPE> callback);
   <TYPE extends BusinessObject<?>> void getQuery(String query, AsyncCallback<List<TYPE>> callback);
   void getAllKondition(AsyncCallback<List<Kondition>> callback);   
   void getAllKreditnehmer(AsyncCallback<List<Kreditnehmer>> callback);
   void getAllKreditvertrage(AsyncCallback<List<Kreditvertrag>> callback);
   
   <TYPE extends BusinessObject<?>> void save(TYPE entity, AsyncCallback<TYPE> callback);
   void save(Kreditvertrag o, AsyncCallback<Kreditvertrag> callback);
   void save(Zahlung o, AsyncCallback<Zahlung> callback);
   void save(Selbstauskunft o, AsyncCallback<Selbstauskunft> callback);
   void save(Kreditnehmer o, AsyncCallback<Kreditnehmer> callback);
}
