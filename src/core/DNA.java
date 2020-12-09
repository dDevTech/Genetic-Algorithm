package core;



public class DNA {
	private Double[] dna;

	public DNA(Double[] dna) {
		this.setDna(dna);
	}

	public DNA() {

	}

	public void addToDNA(int pos,Double c) {
		dna[pos]=c;
	}

	public Double[]  getDna() {
		return dna;
	}

	public void setDna(Double[]  dna) {
		this.dna = dna;
	}

	@Override
	public String toString() {
		if(dna == null) return "Null";
		String s ="";
		for(int i=0;i<dna.length;i++) {
			s+=dna[i]+"   ";
		}
		return s;
	}
	

}
