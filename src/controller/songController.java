// By: Havan Patel

package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.StringTokenizer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Song;
import model.SongNameComparator;

public class songController {

	@FXML Label songDetails;
	@FXML Button delete, cancel, add, addSong, edit, update;
	@FXML TextField name, artist, album, year;
	@FXML ListView<Song> songList;
	private boolean t = true, f = false;
	private ObservableList<Song> obsList;
	private ArrayList<Song> songCollection = new ArrayList<>();

	public void start(Stage mainStage) {
		readData();
		songList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Song>() {
			@Override
			public void changed(ObservableValue<? extends Song> observable, Song oldValue, Song newValue) {
				buttonVisiblity(f, f, t, f, t, t);
				showSongDetails();
			}
		});
	}

	@FXML
	protected void handleButtonAction(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			if (button.equals(addSong)) {
				songList.setMouseTransparent(true);
				buttonVisiblity(f, t, t, t, f, t);
				changeSongDetail("Enter Song Details");
				canEditTextField(true);
				clearTextFields();
				addSong.setDisable(true);
				delete.setDisable(true);
			} else if (button.equals(add)) {
				if (checkEmptyFields()) {
					showDialog("Song name and artist must be filled", 1);
				} else {
					addSong(name.getText(), artist.getText(), album.getText(), year.getText());
					changeSongDetail("Song Details");
				}
			} else if (button.equals(cancel)) {
				clearTextFields();
				canEditTextField(false);
				changeSongDetail("Song Details");
				addSong.setDisable(false);
				buttonVisiblity(f, f, t, f, f, t);
				showSongDetails();
				songList.setMouseTransparent(false);
				delete.setDisable(false);
			} else if (button.equals(delete)) {
				showDialog("Are You Sure you want to delete this song?", 2);
			} else if (button.equals(edit)) {
				songList.setMouseTransparent(true);
				delete.setDisable(true);
				changeSongDetail("Edit Song Details");
				canEditTextField(true);
				addSong.setDisable(true);
				buttonVisiblity(t, t, t, f, f, t);

			} else if (button.equals(update)) {
				int index = songList.getSelectionModel().getSelectedIndex();
				if (index < 0)
					return;
				update(index);
				songList.setMouseTransparent(false);
			}
		}
	}

	public void update(int index) {
		if (dontContainDelim() == true) {
			showDialog("Make sure fields don't contain this characters --> '~+' for saving purpose", 1);
			return;
		}
		if (checkEmptyFields()) {
			showDialog("Song name and artist must be filled", 1);
			return;
		} 
		String newName = name.getText();
		String newArtist = artist.getText();
		String newAlbum = album.getText();
		String sYear = year.getText();
		int newYear;
		if (sYear.isEmpty()) {
			newYear = 0;
		} else {
			try {
				newYear = Integer.parseInt(year.getText());
			} catch (Exception e) {
				showDialog("Enter numbers 0-9 in the song year field", 1);
				return;
			}
		}

		if (!newName.equalsIgnoreCase(songCollection.get(index).getSongName())
				|| !newArtist.equalsIgnoreCase(songCollection.get(index).getSongArtist())) {
			for (Song s : songCollection) {
				if (s.getSongName().equalsIgnoreCase(newName) && s.getSongArtist().equalsIgnoreCase(newArtist)) {
					showDialog("Song with same name and artist already exists", 1);
					delete.setDisable(true);
					changeSongDetail("Edit Song Details");
					return;
				}
			}
		}
		songCollection.get(index).setSongArtist(newArtist);
		songCollection.get(index).setSongName(newName);
		songCollection.get(index).setSongYear(newYear);
		songCollection.get(index).setSongAlbum(newAlbum);
		sort();
		obsList.removeAll(obsList);
		for (int i = 0; i < songCollection.size(); i++) {
			obsList.add(songCollection.get(i));
		}
		showSongDetails();
		songList.getSelectionModel().select(index);
		addSong.setDisable(false);
		delete.setDisable(false);
		changeSongDetail("Song Details");
		buttonVisiblity(f, f, t, f, t, t);
	}

	public void addSong(String name, String artist, String album, String year) {
		Song newSong = null;
		if (dontContainDelim() == true) {
			showDialog("Make sure fields don't contain this characters --> '~+' for saving purpose", 1);
			return;
		}
		if (album.equals("") && year.equals("")) {
			newSong = new Song(name, artist, album, 0);
		} else if (album.equals("")) {
			newSong = new Song(name, artist, album, Integer.parseInt(year));
		} else if (year.equals("")) {
			newSong = new Song(name, artist, album, 0);
		} else {
			try {
				newSong = new Song(name, artist, album, Integer.parseInt(year));
			} catch (Exception e) {
				showDialog("Enter numbers 0-9 in the song year field", 1);
				return;
			}
		}
		for (Song song : songCollection) {
			if (song.getSongName().equalsIgnoreCase(newSong.getSongName())
					&& song.getSongArtist().equalsIgnoreCase(newSong.getSongArtist())) {
				showDialog("Same song with name and artist exists", 1);
				return;
			}
		}
		delete.setDisable(false);
		addSong.setDisable(false);
		songList.setMouseTransparent(false);
		songCollection.add(newSong);
		sort();
		songList.getSelectionModel().select(newSong);
		buttonVisiblity(f, f, t, f, t, t);
	}

	private void showSongDetails() {
		canEditTextField(false);
		if (songCollection.isEmpty()) {
			buttonVisiblity(f, f, t, f, f, t);
		} else {
			buttonVisiblity(f, f, t, f, t, t);
		}
		int index = songList.getSelectionModel().getSelectedIndex();
		if (index < 0)
			return;
		int years = songCollection.get(index).getSongYear();
		songList.refresh();
		name.setText(songCollection.get(index).getSongName());
		artist.setText(songCollection.get(index).getSongArtist());
		album.setText(songCollection.get(index).getSongAlbum());
		if (years == 0) {
			year.setText("");
		} else {
			year.setText(Integer.toString(songCollection.get(index).getSongYear()));
		}
	}

	public void sort() {
		Collections.sort(songCollection, new SongNameComparator());
		obsList = FXCollections.observableArrayList(songCollection);
		songList.setItems(obsList);
	}

	public void saveData() {
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter("data.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (Song song : songCollection) {
			printWriter.println(song.dataFormart());
		}
		printWriter.close();
	}

	public void readData() {

		File file = new File("data.txt");
		if(!file.exists())
			return;
		try {

			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, "~+~", true);

				token.nextToken();
				token.nextToken();
				token.nextToken();

				String name = token.nextToken();

				token.nextToken();
				token.nextToken();
				token.nextToken();
				String artist = token.nextToken();

				token.nextToken();
				token.nextToken();
				token.nextToken();
				String album = token.nextToken();
				if (album.equals("~") == true) {
					album = " ";
					token.nextToken();
					token.nextToken();
				} else {
					token.nextToken();
					token.nextToken();
					token.nextToken();
				}
				String year = token.nextToken();
				if (year.equals("~") == true) {
					year = "0";
				}
				Song temp = new Song(name, artist, album, Integer.parseInt(year));
				songCollection.add(temp);
			}
			bufferedReader.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		obsList = FXCollections.observableArrayList(songCollection);
		songList.setItems(obsList);
		songList.getSelectionModel().select(0);
		showSongDetails();
	}

	public void showDialog(String message, int type) {
		if (type == 1) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText(null);
			alert.setContentText(message);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				int index = songList.getSelectionModel().getSelectedIndex();
				if (index < 0)
					return;
				songCollection.remove(index);
				sort();
				clearTextFields();
				if (songCollection.size() == 1) {
					songList.getSelectionModel().select(0);
				} else if (songCollection.size() == index) {
					songList.getSelectionModel().select(index - 1);
				} else {
					songList.getSelectionModel().select(index);
				}
				showSongDetails();
				canEditTextField(false);
				changeSongDetail("Song Details");
			} else {
				alert.close();
			}
		}
	}

	public void changeSongDetail(String text) {
		songDetails.setText(text);
	}

	public void canEditTextField(boolean b) {
		name.setEditable(b);
		artist.setEditable(b);
		album.setEditable(b);
		year.setEditable(b);
	}

	private void clearTextFields() {
		name.setText("");
		artist.setText("");
		album.setText("");
		year.setText("");
	}

	public void buttonVisiblity(boolean t1, boolean t2, boolean t3, boolean t4, boolean t5, boolean t6) {
		update.setVisible(t1);
		cancel.setVisible(t2);
		addSong.setVisible(t3);
		add.setVisible(t4);
		edit.setVisible(t5);
		delete.setVisible(t6);
	}

	public boolean dontContainDelim() {
		if (artist.getText().contains("~") || name.getText().contains("~") || album.getText().contains("~")
				|| year.getText().contains("~") || artist.getText().contains("+") || name.getText().contains("+")
				|| album.getText().contains("+") || year.getText().contains("+")) {
			return true;
		}
		return false;
	}

	private boolean checkEmptyFields() {
		if (name.getText().equals("") || artist.getText().equals("")) {
			return true;
		}
		return false;
	}
}
