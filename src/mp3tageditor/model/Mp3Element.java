/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3tageditor.model;

import java.io.File;
import java.util.Objects;

/**
 *
 * @author utilizador
 */
public class Mp3Element {
  // "File Name", "New File Name", "Disc #", "Track #", "Title", "Artist", "Album"
  private File mp3File;
  private String fileName;
  private String newFileName;
  private Integer trackNumber;
  private String title;
  private String artist;
  private String album;

  public Mp3Element() {
    this.mp3File = null;
    this.fileName = null;
    this.newFileName = null;
    this.trackNumber = null;
    this.title = null;
    this.artist = null;
    this.album = null;
  }

  public Mp3Element(File mp3File, String fileName, String newFileName, Integer trackNumber, String title, String artist, String album) {
    this.mp3File = mp3File;
    this.fileName = fileName;
    this.newFileName = newFileName;
    this.trackNumber = trackNumber;
    this.title = title;
    this.artist = artist;
    this.album = album;
  }

  public File getMp3File() {
    return mp3File;
  }

  public void setMp3File(File mp3File) {
    this.mp3File = mp3File;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getNewFileName() {
    return newFileName;
  }

  public void setNewFileName(String newFileName) {
    this.newFileName = newFileName;
  }

  public Integer getTrackNumber() {
    return trackNumber;
  }

  public void setTrackNumber(Integer trackNumber) {
    this.trackNumber = trackNumber;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 37 * hash + Objects.hashCode(this.mp3File);
    hash = 37 * hash + Objects.hashCode(this.fileName);
    hash = 37 * hash + Objects.hashCode(this.newFileName);
    hash = 37 * hash + Objects.hashCode(this.trackNumber);
    hash = 37 * hash + Objects.hashCode(this.title);
    hash = 37 * hash + Objects.hashCode(this.artist);
    hash = 37 * hash + Objects.hashCode(this.album);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Mp3Element other = (Mp3Element) obj;
    if (!Objects.equals(this.fileName, other.fileName)) {
      return false;
    }
    if (!Objects.equals(this.newFileName, other.newFileName)) {
      return false;
    }
    if (!Objects.equals(this.title, other.title)) {
      return false;
    }
    if (!Objects.equals(this.artist, other.artist)) {
      return false;
    }
    if (!Objects.equals(this.album, other.album)) {
      return false;
    }
    if (!Objects.equals(this.mp3File, other.mp3File)) {
      return false;
    }
    if (!Objects.equals(this.trackNumber, other.trackNumber)) {
      return false;
    }
    return true;
  }
  
}
