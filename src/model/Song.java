// By: Havan Patel, TRAVIS PULLIS B

package model;

public class Song {

	private String songName;
	private String songArtist;
	private String songAlbum;
	private int songYear;

	public Song(String songName, String songArtist, String songAlbum, int songYear) {
		this.songName = songName;
		this.songArtist = songArtist;
		this.songAlbum = songAlbum;
		this.songYear = songYear;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSongArtist() {
		return songArtist;
	}

	public void setSongArtist(String songArtist) {
		this.songArtist = songArtist;
	}

	public String getSongAlbum() {
		return songAlbum;
	}

	public void setSongAlbum(String songAlbum) {
		this.songAlbum = songAlbum;
	}

	public int getSongYear() {
		return songYear;
	}

	public void setSongYear(int songYear) {
		this.songYear = songYear;
	}
	
	public String getBothSongName_Artist() {
		return songName + songArtist;
	}

	public String dataFormart() {
		return "~+~" + getSongName() + "~+~" + getSongArtist() + "~+~" + getSongAlbum() + "~+~" + getSongYear() + "~+~";
	}

	public String toString() {
		return songName + " By " + songArtist;
	}
}
