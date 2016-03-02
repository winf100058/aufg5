package de.fhwedel.om.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.fhwedel.om.model.BusinessObject;
import de.fhwedel.om.model.Kondition;
import de.fhwedel.om.model.Kreditnehmer;
import de.fhwedel.om.model.Kreditvertrag;
import de.fhwedel.om.model.Selbstauskunft;
import de.fhwedel.om.model.Zahlung;
import de.fhwedel.om.services.OMService;

@SuppressWarnings("serial")
public class OMServiceImpl 
extends RemoteServiceServlet 
implements OMService {
    
   private static Properties props = new Properties();
   private static EntityManager em;
   
   protected static EntityManager getEM() {
      if(OMServiceImpl.em == null) {
         try {
            if(props.getProperty("regenerate", "0").equals("1")) {
               props.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
               props.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION); 
            }
            
            EntityManagerFactory emf      = Persistence.createEntityManagerFactory(OMServiceImpl.props.getProperty("persistence_unit", "pu"), props);
            if(emf != null) {
               OMServiceImpl.em           = emf.createEntityManager();
               OMServiceImpl.em.setFlushMode(FlushModeType.COMMIT);         
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return OMServiceImpl.em;
   }
   
   static {      
      OMServiceImpl.props = new Properties();
      try {
         OMServiceImpl.props.load( new FileInputStream("om.conf") );
      } catch (IOException e) {
         System.err.println("Failed to load configuration file (" + new File(".\\om.conf").getAbsolutePath() + "), continuing with defaults...");
      }
      
      if(OMServiceImpl.props.getProperty("regenerate", "0").equals("1")) {            
         EntityManager em = OMServiceImpl.getEM();
         em.getTransaction().begin();
         // TODO: Hier 5 Beispiel Konditionen erstellen
         long milisec = 14516028 * 100000;
         em.persist(new Kondition(1, 12, 1000, 2000, 100,200, new Date(milisec), 500));
         em.persist(new Kondition(1, 24, 3000, 4000, 100,300, new Date(milisec), 400));
         em.persist(new Kondition(1, 36, 5000, 6000, 100,400, new Date(milisec), 300));
         em.persist(new Kondition(1, 48, 7000, 8000, 100,500, new Date(milisec), 200));
         em.persist(new Kondition(1, 60, 9000, 10000, 100,600, new Date(milisec), 100));
         em.getTransaction().commit();
      }      
   }

   @SuppressWarnings("unchecked")
   @Override
   synchronized public List<Kreditnehmer> getAllKreditnehmer() {
      EntityManager em = OMServiceImpl.getEM();
      return em.createNamedQuery("getAllKreditnehmer").getResultList();      
   }
    
   @SuppressWarnings("unchecked")
   @Override
   synchronized public List<Kondition> getAllKondition() {       
      EntityManager em = OMServiceImpl.getEM();
      return em.createNamedQuery("getAllKondition").getResultList();      
   }
   
   @SuppressWarnings("unchecked")
   @Override
   synchronized public List<Kreditvertrag> getAllKreditvertrage() {       
      EntityManager em = OMServiceImpl.getEM();
      return em.createNamedQuery("getAllKreditvertrage").getResultList();      
   }
   
   @SuppressWarnings("unchecked")
   synchronized public <TYPE extends BusinessObject<?>> List<TYPE> getQuery(String query) {
      EntityManager em  = OMServiceImpl.getEM();
      return (List<TYPE>)em.createNamedQuery(query).getResultList();
   }

   @SuppressWarnings("unchecked")
   synchronized public <TYPE extends BusinessObject<?>> TYPE get(String cls, Serializable id) {
      Class<?> cl = null;
      try {
         cl = Class.forName(cls);
      } catch (ClassNotFoundException e) {         
         e.printStackTrace();
      }
      EntityManager em = OMServiceImpl.getEM();
      return (TYPE)em.find(cl, id);
   }
   
   @Override
   synchronized public <TYPE extends BusinessObject<?>> TYPE save(TYPE entity) {
      EntityManager em = OMServiceImpl.getEM();
      em.getTransaction().begin();

      if(entity.getID() != null)
         em.merge(entity);
      else
         em.persist(entity);         
      em.getTransaction().commit();
      return entity;
   }
   
   @Override
   synchronized public Kreditvertrag save(Kreditvertrag o) {
      EntityManager em = OMServiceImpl.getEM();
      List<Kondition> listKonditionen = new LinkedList<Kondition>();
      listKonditionen = this.getAllKondition();
      boolean foundKondition = false;
      em.getTransaction().begin();
      if(o.getID() != null)
         o = em.merge(o);
      else {
         em.persist(o);
         o.calcVertragnr(this.getAllKreditvertrage().size() + 1);
         Kreditnehmer c = o.getKreditnehmer();
         c.getKreditvertrage().add(o);
        for(int i = 0; i < listKonditionen.size() && !foundKondition; i ++) {
        	 if(listKonditionen.get(i).isPassend(o)) {
        		 o.setZinssatz(listKonditionen.get(i).getZinssatz());
        		 foundKondition = true;
        	 }
         }
         if(!foundKondition) {
        	 o.setZinssatz(0);
         }
         em.merge(c);
      }
      em.getTransaction().commit();
      return o;
   }
   
   @Override
   synchronized public Zahlung save(Zahlung o) {
      EntityManager em = OMServiceImpl.getEM();
      em.getTransaction().begin();

      if(o.getID() != null)
         o = em.merge(o);
      else {
         em.persist(o);
         Kreditvertrag c = o.getKreditvertrag();
         c.getZahlung() .add(o);
         em.merge(c);
      }
      em.getTransaction().commit();
      return o;
   }
   
   @Override
   synchronized public Selbstauskunft save(Selbstauskunft o) {
      EntityManager em = OMServiceImpl.getEM();
      em.getTransaction().begin();

      if(o.getID() != null) {
    	 o.calcBonitatindex();
         o = em.merge(o);
         o.calcMaxKreditsum();
      }
      else {
         em.persist(o);
         Kreditnehmer c = o.getKreditnehmer();
         c.setSelbstauskunft(o);
         o.calcBonitatindex();
         o.calcMaxKreditsum();
         em.merge(c);
      }
      em.getTransaction().commit();
      return o;
   }
   
   @Override
   synchronized public Kreditnehmer save(Kreditnehmer o) {
      EntityManager em = OMServiceImpl.getEM();
      em.getTransaction().begin();

      if(o.getID() != null)
         o = em.merge(o);
      else {
         em.persist(o);
         o.calcKundNr(this.getAllKreditnehmer().size() +1);
      }
      em.getTransaction().commit();
      return o;
   }
   
   
}