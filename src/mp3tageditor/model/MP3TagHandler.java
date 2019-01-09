/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3tageditor.model;

import java.io.File;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;

/**
 *
 * @author Ricardoc
 */
public class MP3TagHandler {
    private File sourceFile;
    private MP3File mp3File;
    private AbstractMP3Tag mp3Tags;

    public MP3TagHandler(File sourceFile) throws Exception {
        this.sourceFile = sourceFile;
        this.setMP3File();
    }

    public MP3TagHandler() {
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
            this.mp3File.getID3v1Tag().setSongTitle(title);
            this.mp3File.save();
        }
    }
   
    public String getSongTitle() throws Exception {
       String res = null;
       if (this.mp3File!=null) {
           res = this.mp3File.getID3v1Tag().getSongTitle();
       }
       return res;
    }    
    
}
