package sk.upjs.paz1c.guideman.storage;

import java.sql.Blob;
import java.util.Objects;

public class Tour {

	private Long id;
	private String title;
	private String bio;
	private Long maxSlots;
	private Long locationId;
	private Long guidemanId;
	private Blob image;

	public Tour() {

	}

	public Tour(Long id, String title, String bio, Long maxSlots, Long location, Long guideman, Blob image) {
		this.id = id;
		this.title = title;
		this.bio = bio;
		this.maxSlots = maxSlots;
		this.locationId = location;
		this.guidemanId = guideman;
		this.image = image;
	}

	public Tour(String title, String bio, Long maxSlots, Long locationId, Long guidemanId, Blob image) {
		this.title = title;
		this.bio = bio;
		this.maxSlots = maxSlots;
		this.locationId = locationId;
		this.guidemanId = guidemanId;
		this.image = image;
	}

	public Tour(Long id, String title, String bio, Long maxSlots, Long locationId, Long guidemanId) {
		this.id = id;
		this.title = title;
		this.bio = bio;
		this.maxSlots = maxSlots;
		this.locationId = locationId;
		this.guidemanId = guidemanId;
	}

	public Tour(Long id, String title, Long maxSlots, Long locationId, Long guidemanId) {
		this.id = id;
		this.title = title;
		this.maxSlots = maxSlots;
		this.locationId = locationId;
		this.guidemanId = guidemanId;
	}

	public Tour(Long id, String title, Long maxSlots) {
		this.id = id;
		this.title = title;
		this.maxSlots = maxSlots;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public Long getMaxSlots() {
		return maxSlots;
	}

	public void setMaxSlots(Long maxSlots) {
		this.maxSlots = maxSlots;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public Long getGuidemanId() {
		return guidemanId;
	}

	public void setGuidemanId(Long guidemanId) {
		this.guidemanId = guidemanId;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "Tour [id=" + id + ", title=" + title + ", bio=" + bio + ", maxSlots=" + maxSlots + ", location_id="
				+ locationId + ", guideman_id=" + guidemanId + ", image=" + image + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(bio, guidemanId, id, image, locationId, maxSlots, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tour other = (Tour) obj;
		return Objects.equals(bio, other.bio) && Objects.equals(guidemanId, other.guidemanId)
				&& Objects.equals(id, other.id) && Objects.equals(image, other.image)
				&& Objects.equals(locationId, other.locationId) && Objects.equals(maxSlots, other.maxSlots)
				&& Objects.equals(title, other.title);
	}
	
	
}
