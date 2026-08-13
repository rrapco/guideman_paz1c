package sk.upjs.paz1c.guideman.storage;

import java.util.Objects;

public class Location {

	private Long id;
	private String country;
	private String city;
	private String street;
	private Integer street_number;

	public Location() {

	}

	public Location(Long id, String country, String city, String street, Integer street_number) {
		this.id = id;
		this.country = country;
		this.city = city;
		this.street = street;
		this.street_number = street_number;
	}

	public Location(String country, String city, String street, Integer street_number) {
		this.country = country;
		this.city = city;
		this.street = street;
		this.street_number = street_number;
	}

	public Location(Long id, String country, String city, String street) {
		this.id = id;
		this.country = country;
		this.city = city;
		this.street = street;
	}

	public Location(String country, String city, String street) {
		this.country = country;
		this.city = city;
		this.street = street;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Integer getStreet_number() {
		return street_number;
	}

	public void setStreet_number(Integer street_number) {
		this.street_number = street_number;
	}

	@Override
	public String toString() {
		return "Location [id=" + id + ", country=" + country + ", city=" + city + ", street=" + street
				+ ", street_number=" + street_number + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(city, country, id, street, street_number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(city, other.city) && Objects.equals(country, other.country)
				&& Objects.equals(id, other.id) && Objects.equals(street, other.street)
				&& Objects.equals(street_number, other.street_number);
	}
	
	

}
