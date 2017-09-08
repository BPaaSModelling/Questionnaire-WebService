package ch.fhnw.bpaas.model.modellingenvironment;

import java.util.ArrayList;

public class PaletteElement {

	String id;
	String label;
	String imageURL;
	String thumbnailURL;
	String representedClass;	//It might be bpmn:Task
	String labelRepresented;	//It should  be Task
	int labelSizeX2;
	int labelSizeY2;
	boolean showedInPalette;
	String paletteCategory;
	ArrayList<PaletteElement> childPaletteElements;
	
	
}
