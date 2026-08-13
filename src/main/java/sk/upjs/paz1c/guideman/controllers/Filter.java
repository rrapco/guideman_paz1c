package sk.upjs.paz1c.guideman.controllers;

public enum Filter {

	INSTANCE;

	private String country;
	private String month;
	private String guideman;
	private String price;
	private boolean newFilters;

	public String getCountry() {
		System.out.println(country);
		return country;
	}

	public String getMonth() {
		return month;
	}

	public String getGuideman() {
		return guideman;
	}

	public String getPrice() {
		return price;
	}

	public boolean getNewFilters() {
		if (newFilters == true) {
			return true;
		}
		return false;
	}

	public void setCountry(String country) {
		System.out.println("setting country : " + country);
		this.country = country;
	}

	public void setMonth(String month) {
		System.out.println("setting month : " + month);
		this.month = month;
	}

	public void setGuideman(String guideman) {
		System.out.println("setting guideman : " + guideman);
		this.guideman = guideman;
	}

	public void setPrice(String price) {
		System.out.println("setting price : " + price);
		this.price = price;
	}

	public void setNewFilters(boolean filter) {
		if (filter == true) {
			this.newFilters = true;
		} else {
			this.newFilters = false;
		}

	}

}
