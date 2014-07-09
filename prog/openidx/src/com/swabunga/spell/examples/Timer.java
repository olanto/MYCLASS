package com.swabunga.spell.examples;

/** 
 * Gestion de timer en milli seconde.
* Une classe pour déclencher un chronomètre et pour mesurer facilement l'efficacité du code.
* Par exemple: 
* <pre> 
* Timer t1 = new Timer("section A du code");  // le chrono a démarrer!
*    ...section A ... 
* t1.stop(); // affiche le temps en milliseconde ...
* </pre> 
* On peut réutiliser le même chronomètre plusieurs fois, avec la méthode restart()
* @author Jacques Guyot 
* @version 1.1 
*/ 
public class Timer{

private long start;
private String activity;

/**
* crée un chronomètre. Et puis le démarre et affiche dans 
* la console le commentaire associé
* 
* @param s le commentaire associé au chrono. 
*/
  public Timer(String s){
	   activity=s;
	   start=System.currentTimeMillis();
	   System.out.println("START: "+activity);
	}

/**
* stope le chronomètre. Et affiche dans la console le commentaire associé
* et le temps mesuré en milliseconde
* 
*/ 

   public void  stop() {
		start=System.currentTimeMillis()-start;
		 System.out.println("STOP: "+activity+" - "+start+" ms");
	}
   /**
    * redémarre le chronomètre. Et affiche dans la console le commentaire associé
    * @param s le commentaire associé avec le chronomètre
    */ 
	public void  restart(String s) {
	   activity=s;
	   start=System.currentTimeMillis();
	   System.out.println("START: "+activity);
	}
	}