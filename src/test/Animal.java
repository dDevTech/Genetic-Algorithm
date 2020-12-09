package test;

import core.DNA;

public class Animal extends DNA {
	private double value;

	public Animal(double value) {
		this.value = value;
	}

	public Animal() {

	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Animal [value=" + value + " DNA: " + super.toString() + "]";
	}

}
