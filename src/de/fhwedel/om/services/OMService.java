package de.fhwedel.om.services;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.fhwedel.om.model.Kondition;
import de.fhwedel.om.model.BusinessObject;
import de.fhwedel.om.model.Kreditnehmer;
import de.fhwedel.om.model.Kreditvertrag;
import de.fhwedel.om.model.Selbstauskunft;
import de.fhwedel.om.model.Zahlung;

@RemoteServiceRelativePath("services")
public interface OMService extends RemoteService {
   
   public <TYPE extends BusinessObject<?>> TYPE get(String cls, Serializable id);
   public <TYPE extends BusinessObject<?>> List<TYPE> getQuery(String query);
   public List<Kondition> getAllKondition();
   public List<Kreditnehmer> getAllKreditnehmer();
   public List<Kreditvertrag> getAllKreditvertrage();
   
   public <TYPE extends BusinessObject<?>> TYPE save(TYPE entity);
   public Kreditvertrag save(Kreditvertrag o);
   public Zahlung save(Zahlung o);
   public Selbstauskunft save(Selbstauskunft o);
   public Kreditnehmer save(Kreditnehmer o);
}
