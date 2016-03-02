package de.fhwedel.om.widgets;

public interface Identifier<VALUE, KEY> {
   public KEY identify(VALUE obj);
}