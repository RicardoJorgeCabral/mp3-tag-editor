/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3tageditor.model;

import java.io.File;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.ID3v1;

/**
 *
 * @author Ricardoc
 */
public class Mp3TagHandler {
    private File sourceFile;
    private MP3File mp3File;
    private ID3v1 mp3Tags;

    public Mp3TagHandler(File sourceFile) throws Exception {
        this.sourceFile = sourceFile;
        this.setMP3File();
    }

    public Mp3TagHandler() {
        this.nullify();
    }
    
    private void nullify() {
        this.sourceFile = null;
        this.mp3File = null;
        this.mp3Tags = null;        
    }
    
    private void setMP3File() throws Exception {
        if (this.sourceFile!=null && this.sourceFile.exists() && this.sourceFile.isFile()) {
            this.mp3File = new MP3File(this.sourceFile);
            this.mp3Tags = this.mp3File.getID3v1Tag();
        }
        else {
            this.nullify();
            throw new Exception("Not a valid source MP3 file!");
        }        
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) throws Exception {
        this.sourceFile = sourceFile;
        this.setMP3File();
    }
    
    public void setSongTitle(String title) throws Exception {        
        if (this.mp3File!=null) {            
            if (this.mp3Tags==null) {                
                this.mp3Tags = new ID3v1();
                System.out.println("New ID3v1 created!");
            }
            this.mp3Tags.setTitle(title);
            this.mp3File.setID3v1Tag(this.mp3Tags);            
            this.mp3File.save(this.sourceFile);
        }
    }
   
    public String getTitle() throws Exception {
       String res = null;
       if (this.mp3Tags!=null) {
           res = this.mp3Tags.getTitle();
       }
       return res;        
    }
    
    public String getSongTitle() throws Exception {
       String res = null;
       if (this.mp3Tags!=null) {
           res = this.mp3Tags.getSongTitle();
       }
       return res;
    }  
    
    public Integer getTrackNumber() throws Exception {
      String res = null;
      Integer num = null;
      if (this.mp3Tags!=null) {
        res = this.mp3Tags.getTrackNumberOnAlbum();
        num = Integer.parseInt(res);
      }
      return num;
    }
    
    public String getArtist() throws Exception {
      String res = null;
      if (this.mp3Tags!=null) {
          res = this.mp3Tags.getArtist();
      }
      return res;
    }
    
    public String getAlbum() throws Exception {
      String res = null;
      if (this.mp3Tags!=null) {
          res = this.mp3Tags.getAlbum();
      }
      return res;
    }
}
