package fr.eats.commands.objects;

public class Meals {
	private Integer Price;
	private Integer AdherencePrice;
	private String name;
	private boolean available;

	public Meals(Integer price, Integer adherencePrice, String name){
		Price = price;
		AdherencePrice = adherencePrice;
		this.name = name;
		this.available = true;
	}

	public Integer getPrice() {
		return Price;
	}

	public void setPrice(Integer price) {
		Price = price;
	}

	public Integer getAdherencePrice() {
		return AdherencePrice;
	}

	public void setAdherencePrice(Integer adherencePrice) {
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
