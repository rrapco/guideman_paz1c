package sk.upjs.paz1c.guideman.models;

import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.User;

public class UserFxModel {

	private Long id;
	private StringProperty name = new SimpleStringProperty();
	private StringProperty surname = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	private StringProperty telNumber = new SimpleStringProperty();
	private ObjectProperty<LocalDate> birthdate = new SimpleObjectProperty<>();
	private StringProperty login = new SimpleStringProperty();
	private StringProperty password = new SimpleStringProperty();
	private Blob image;

	private ObservableList<User> users;

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	
	public UserFxModel() {
		
	}

	public UserFxModel(User user) {
		this.id = user.getId();
		name.set(user.getName());
		surname.set(user.getSurname());
		email.set(user.getEmail());
		if (user.getTelNumber() != null) {
			telNumber.set(user.getTelNumber());
		}
		birthdate.set(LocalDateTime.parse(user.getBirthdate().toString(), formatter).toLocalDate());
		login.set(user.getLogin());
		password.set(user.getPassword());
		if (user.getImage() != null) {
			this.image = user.getImage();
		}

		List<User> list = DaoFactory.INSTANCE.getUserDao().getAll();
		users = FXCollections.observableArrayList(list);

	}

	public ObservableList<User> getUsersModel() {
		return users;
	}

	public List<User> getAll() {
		return new ArrayList<>(users);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty surnameProperty() {
		return surname;
	}

	public String getSurname() {
		return surname.get();
	}

	public void setSurname(String surname) {
		this.surname.set(surname);
	}

	public StringProperty emailProperty() {
		return email;
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
	}

	public StringProperty telNumberProperty() {
		return name;
	}

	public String getTelNumber() {
		return telNumber.get();
	}

	public void setTelNumber(String telNumber) {
		this.telNumber.set(telNumber);
	}

	public ObjectProperty<LocalDate> birthdateProperty() {
		return birthdate;
	}

	public LocalDate getBirthdate() {
		return birthdate.get();
	}

	public void setBirthdate(LocalDate localDate) {
		this.birthdate.set(LocalDateTime.parse(localDate.toString(), formatter).toLocalDate());
	}

	public StringProperty loginProperty() {
		return login;
	}

	public String getLogin() {
		return login.get();
	}

	public void setLogin(String login) {
		this.login.set(login);
	}

	public StringProperty passwordProperty() {
		return password;
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public User getUser() {
		return new User(id, getName(), getSurname(), getEmail(), getTelNumber(), getBirthdate(), getLogin(),
				getPassword(), image);
	}

}
