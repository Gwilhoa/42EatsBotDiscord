package fr.eats.commands.objects;

public class Meals {
	private Double Price;
	private Double AdherencePrice;
	private String name;
	private boolean iswithingredient;
	private boolean available;

	public Meals(Double price, Double adherencePrice, String name, boolean iswithingredient){
		Price = price;
		AdherencePrice = adherencePrice;
		this.name = name;
		this.available = true;
		this.iswithingredient = iswithingredient;
	}

	public boolean isIswithingredient() {
		return iswithingredient;
	}

	public Double getPrice() {
		return Price;
	}

	public void setPrice(Double price) {
		Price = price;
	}

	public Double getAdherencePrice() {
		return AdherencePrice;
	}

	public void setAdherencePrice(Double adherencePrice) {
		AdherencePrice = adherencePrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
}
