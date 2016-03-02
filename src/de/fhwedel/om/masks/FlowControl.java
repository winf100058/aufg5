package de.fhwedel.om.masks;


public interface FlowControl {
   
   public void update(BusinessMask<?> mask);
    
   public void forward(BusinessMask<?> mask);
    
   public void backward();

}
