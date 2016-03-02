package de.fhwedel.om.model;

import java.io.Serializable;

public interface BusinessObject<KEY> extends Serializable {
  
   public KEY getID();

   public String getCaption();
     
}
