/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3tageditor.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import mp3tageditor.model.Mp3Element;
import mp3tageditor.model.Mp3TagHandler;

/**
 *
 * @author utilizador
 */
public class Main extends javax.swing.JFrame {

  final JFileChooser fc = new JFileChooser();  
  private List<Mp3Element> mp3List = new ArrayList<Mp3Element>();
  /**
   * Creates new form Main
   */
  public Main() {
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.setAcceptAllFileFilterUsed(false);
    initComponents();
    this.formatFilesTable();
    
    jTableFiles.getModel().addTableModelListener((TableModelEvent ev) -> {
      if (ev.getType() == TableModelEvent.UPDATE) {
        System.out.println("Cell " + ev.getFirstRow() + ", " + ev.getColumn() + " changed."
                + " The new value: " + jTableFiles.getModel().getValueAt(ev.getFirstRow(), ev.getColumn()));
      }
    });
    
  }
  
  private void formatFilesTable() {
    TableColumn column = null;
    for (int i = 0; i < 5; i++) {
      column = this.jTableFiles.getColumnModel().getColumn(i);
      if (i == 2) {
        column.setPreferredWidth(20);
      } else {
        column.setPreferredWidth(200);
      }
    }
  }
  /*
  private void initFilesTable() {
    FileListTableModel model = new FileListTableModel();
    Object[][] data = new Object[this.mp3List.size()][model.getColumnCount()];
    int i = 0;
    for (Mp3Element mp3 : this.mp3List) {
       //"File Name", "New File Name", "Track #", "Title", "Artist", "Album"
      data[i][0] = mp3.getFileName();
      data[i][1] = mp3.getNewFileName();
      data[i][2] = mp3.getTrackNumber();
      data[i][3] = mp3.getTitle();
      data[i][4] = mp3.getArtist();
      data[i][5] = mp3.getAlbum();
      i++;
    }
    model.setData(data);
    this.jTableFiles = new JTable(model);    
    
  }
  */
  private FileListTableModel fillFilesTable() {    
    Object[][] data = new Object[this.mp3List.size()][6];
    int i = 0;
    for (Mp3Element mp3 : this.mp3List) {
      data[i][0] = mp3.getFileName();
      data[i][1] = mp3.getNewFileName();
      data[i][2] = mp3.getTrackNumber();
      data[i][3] = mp3.getTitle();
      data[i][4] = mp3.getArtist();
      data[i][5] = mp3.getAlbum();      
      /*
      this.jTableFiles.getModel().setValueAt(mp3.getFileName(), i, 0);
      this.jTableFiles.getModel().setValueAt(mp3.getNewFileName(), i, 1);
      this.jTableFiles.getModel().setValueAt(mp3.getTrackNumber(), i, 2);
      this.jTableFiles.getModel().setValueAt(mp3.getTitle(), i, 3);
      this.jTableFiles.getModel().setValueAt(mp3.getArtist(), i, 4);
      this.jTableFiles.getModel().setValueAt(mp3.getAlbum(), i, 5);      
      */
      i++;
    }
    FileListTableModel model = new FileListTableModel(data);
    model.addTableModelListener((TableModelEvent ev) -> {
      if (ev.getType() == TableModelEvent.UPDATE) {
        int row = ev.getFirstRow();
        int col = ev.getColumn();
        if ( (col==2) || (col==3) ) {
          // Track number changed
          String newFileName = String.format("%02d",jTableFiles.getModel().getValueAt(row,2)) + "." + jTableFiles.getModel().getValueAt(row,3);
          jTableFiles.getModel().setValueAt(newFileName, row, 1);
        }
      }
    });
    return model;
  }
  
  private boolean preChangesVerification() {
    for (Mp3Element el : this.mp3List) {
      File fileOrig = el.getMp3File();
      File fileDest = new File(fileOrig.getParent() + "\\" + el.getNewFileName() + ".mp3");
      //System.out.println(fileOrig.getAbsolutePath() + " --> " + fileDest.getAbsolutePath());
      if ( (fileDest.exists()) && (!fileOrig.getAbsoluteFile().equals(fileDest.getAbsoluteFile())) ) {
        String msg = "Cannot rename file to " + el.getNewFileName() + "!\nA file with the same name exists.";
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);  
        return false;
      }
      for (Mp3Element subEl : this.mp3List) {
        if (!el.equals(subEl)) {
          if (el.getNewFileName().equals(subEl.getFileName())) {
            String msg = "Cannot rename file to " + el.getNewFileName() + "!\nA file with the same name exists.";
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);  
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private boolean processChanges() {
    if (preChangesVerification()) {
      try {
        for (Mp3Element el : this.mp3List) {
          File f = el.getMp3File();
          File newFile = new File(f.getParent() + "\\" + el.getNewFileName() + ".mp3");
          f.renameTo(newFile);
          Mp3TagHandler handler = new Mp3TagHandler();
          handler.setSourceFile(newFile);
          handler.setAlbum(el.getAlbum());
          handler.setArtist(el.getArtist());
          handler.setSongTitle(el.getTitle());
          handler.setTrackNumber(el.getTrackNumber());
        }      
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);      
        ex.printStackTrace();
        return false;
      }
      return true;
    }
    return false;
  }
  
  private void showDataTable() {
    this.mp3List.clear();
      String workingPath = fc.getSelectedFile().toString();
      tfWorkingFolder.setText(workingPath);
      //JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);      
      try (Stream<Path> walk = Files.walk(Paths.get(workingPath))) {
        List<String> files = walk.map(x -> x.toString())
                .filter(f -> f.endsWith(".mp3")).collect(Collectors.toList());
        //result.forEach(System.out::println);
        for (String file : files) {
          File f = new File(file);
          Mp3Element el = new Mp3Element();
          Mp3TagHandler handler = new Mp3TagHandler(f);          
          el.setMp3File(f);
          el.setFileName(f.getName());
          String newFileName = String.format("%02d", handler.getTrackNumber().intValue()) + "." + handler.getSongTitle();
          el.setNewFileName(newFileName);
          el.setTrackNumber(handler.getTrackNumber());
          el.setAlbum(handler.getAlbum());
          el.setArtist(handler.getArtist());
          el.setTitle(handler.getSongTitle());          
          this.mp3List.add(el);
        }
        this.jTableFiles.setModel(fillFilesTable());
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);      
        e.printStackTrace();
      }
  }
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    tfWorkingFolder = new javax.swing.JTextField();
    jButton1 = new javax.swing.JButton();
    jLabel2 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    tfAlbumForList = new javax.swing.JTextField();
    jLabel9 = new javax.swing.JLabel();
    tfArtistForList = new javax.swing.JTextField();
    jButton2 = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    jScrollPane2 = new javax.swing.JScrollPane();
    jTableFiles = new javax.swing.JTable();
    jButton3 = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

    jLabel1.setText("Working folder:");

    tfWorkingFolder.setEditable(false);

    jButton1.setText("Change folder ...");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tfWorkingFolder))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jButton1)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(tfWorkingFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jLabel2.setText("Folder Content:");

    jLabel8.setText("Set Album for List:");

    jLabel9.setText("Set Artist for List:");

    jButton2.setText("Set");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    jTableFiles.setModel(fillFilesTable());
    jScrollPane2.setViewportView(jTableFiles);

    jScrollPane1.setViewportView(jScrollPane2);

    jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
    jButton3.setText("Update MP3 Info");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton3ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton2))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel9)
              .addComponent(jLabel8))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(tfAlbumForList, javax.swing.GroupLayout.DEFAULT_SIZE, 1082, Short.MAX_VALUE)
              .addComponent(tfArtistForList, javax.swing.GroupLayout.DEFAULT_SIZE, 1082, Short.MAX_VALUE)))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(tfAlbumForList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel9)
          .addComponent(tfArtistForList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addComponent(jLabel2))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton2)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton3)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
    int returnVal = fc.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) { 
      this.showDataTable();
    }
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    // TODO add your handling code here:
    if ( (tfAlbumForList.getText().length()>0) ||
         (tfArtistForList.getText().length()>0)) {
      String album = tfAlbumForList.getText();
      String artist = tfArtistForList.getText();
      
      for (Mp3Element it : this.mp3List) {
        if (album.length()>0) {
          it.setAlbum(album);
        }
        if (artist.length()>0) {
          it.setArtist(artist);
        }
      }
      this.jTableFiles.setModel(fillFilesTable());
    }
  }//GEN-LAST:event_jButton2ActionPerformed

  private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    // TODO add your handling code here:
    int dialogResult = JOptionPane.showConfirmDialog (this, "Do you want to process the changes?","Confirm",JOptionPane.YES_NO_OPTION);
    if(dialogResult == JOptionPane.YES_OPTION){
      this.mp3List.clear();
      FileListTableModel model = (FileListTableModel) this.jTableFiles.getModel();
      for (int i=0; i<model.getRowCount(); i++) {
        Mp3Element el = new Mp3Element();
        el.setMp3File(new File((String) model.getValueAt(i, 0)));
        el.setNewFileName((String) model.getValueAt(i, 1));
        el.setTrackNumber((Integer) model.getValueAt(i, 2));
        el.setTitle((String) model.getValueAt(i, 3));
        el.setArtist((String) model.getValueAt(i, 4));
        el.setAlbum((String) model.getValueAt(i, 5));
      }
      this.showDataTable();
      if (this.processChanges()) {
        JOptionPane.showMessageDialog(this,"Changes done!","Info",JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }//GEN-LAST:event_jButton3ActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Main().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTable jTableFiles;
  private javax.swing.JTextField tfAlbumForList;
  private javax.swing.JTextField tfArtistForList;
  private javax.swing.JTextField tfWorkingFolder;
  // End of variables declaration//GEN-END:variables
}

class FileListTableModel extends AbstractTableModel {
  private String[] columnNames = { "File Name", "New File Name", "Track #", "Title", "Artist", "Album" };
  private Object[][] data;
    
  public FileListTableModel(Object[][] list) {
    this.data = list;
  }
  
  public void setData(Object[][] list) {
    this.data = list;
  }
  
  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    return data.length;
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  public Object getValueAt(int row, int col) {
    return data[row][col];
  }

  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  public boolean isCellEditable(int row, int col) {
  //Note that the data/cell address is constant,
  //no matter where the cell appears onscreen.
    if (col < 1) {
        return false;
    } else {
        return true;
    }
  }

  public void setValueAt(Object value, int row, int col) {
    data[row][col] = value;
    fireTableCellUpdated(row, col);
  }
}
