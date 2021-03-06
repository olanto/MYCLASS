/**********
    Copyright � 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myCLASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/


package org.olanto.cat.inbox;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.net.*;
import java.net.*;

public class ClassifyInABox extends javax.swing.JFrame {

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        GlobalPane = new javax.swing.JPanel();
        headerPane = new javax.swing.JPanel();
        titlePane = new javax.swing.JPanel();
        txtHeader = new javax.swing.JTextPane();
        training = new javax.swing.JButton();
        UtilisateurPane = new javax.swing.JPanel();
        jTextPane1 = new javax.swing.JTextPane();
        dirName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaContent = new javax.swing.JTextArea();
        centerPane = new javax.swing.JPanel();
        fileContainer = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        statusPane = new javax.swing.JPanel();
        statusTxt = new javax.swing.JTextPane();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exit(evt);
            }
        });

        GlobalPane.setBackground(new java.awt.Color(0, 0, 0));
        GlobalPane.setPreferredSize(new java.awt.Dimension(300, 200));
        GlobalPane.setLayout(new java.awt.BorderLayout());

        headerPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        headerPane.setLayout(new java.awt.BorderLayout());

        titlePane.setLayout(new java.awt.BorderLayout());

        txtHeader.setBackground(new java.awt.Color(238, 238, 238));
        txtHeader.setBorder(null);
        txtHeader.setEditable(false);
        txtHeader.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtHeader.setText("Assistant de classification - Simple-Shift");
        titlePane.add(txtHeader, java.awt.BorderLayout.CENTER);

        training.setText("training!");
        training.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainingActionPerformed(evt);
            }
        });
        titlePane.add(training, java.awt.BorderLayout.EAST);

        headerPane.add(titlePane, java.awt.BorderLayout.NORTH);

        jTextPane1.setBackground(new java.awt.Color(238, 238, 238));
        jTextPane1.setEditable(false);
        jTextPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTextPane1.setText("training");
        UtilisateurPane.add(jTextPane1);

        dirName.setColumns(20);
        dirName.setText("c:/AAA/INBOX/TRAINING");
        dirName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dirNameActionPerformed(evt);
            }
        });
        UtilisateurPane.add(dirName);

        headerPane.add(UtilisateurPane, java.awt.BorderLayout.CENTER);

        jTextAreaContent.setColumns(20);
        jTextAreaContent.setRows(5);
        jScrollPane2.setViewportView(jTextAreaContent);

        headerPane.add(jScrollPane2, java.awt.BorderLayout.PAGE_END);

        GlobalPane.add(headerPane, java.awt.BorderLayout.NORTH);

        centerPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        centerPane.setLayout(new java.awt.BorderLayout());

        fileContainer.setEditable(false);
        fileContainer.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        fileContainer.setBorder(null);
        fileContainer.setDragEnabled(true);
        fileContainer.setPreferredSize(new java.awt.Dimension(400, 300));
        centerPane.add(fileContainer, java.awt.BorderLayout.CENTER);

        jButton1.setText("classifier ce qui est dans le champ ci-dessus");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        centerPane.add(jButton1, java.awt.BorderLayout.PAGE_START);

        GlobalPane.add(centerPane, java.awt.BorderLayout.CENTER);

        statusPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        statusPane.setLayout(new java.awt.BorderLayout());

        statusTxt.setBackground(new java.awt.Color(238, 238, 238));
        statusTxt.setBorder(null);
        statusTxt.setEditable(false);
        statusTxt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        statusTxt.setText("Veuillez glisser vos fichiers ci-dessus...");
        statusPane.add(statusTxt, java.awt.BorderLayout.CENTER);

        GlobalPane.add(statusPane, java.awt.BorderLayout.SOUTH);

        getContentPane().add(GlobalPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void trainingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainingActionPerformed
        CreateIndexFile.create(dirName.getText(), fileContainer); // TODO add your handling code here:
        System.out.println("end of indexing ...");
        BuildMNN.trainNN(fileContainer);
        Classify.reset();
    }//GEN-LAST:event_trainingActionPerformed

    private void dirNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dirNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dirNameActionPerformed

    private void exit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exit
    }//GEN-LAST:event_exit

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        fileContainer.setText("");

        fileContainer.append("classify from field:\n");

        String toBeclassify = jTextAreaContent.getText();
        fileContainer.append(Classify.advise(toBeclassify));
       fileContainer.append("\n\ncontent:\n");
        fileContainer.append(toBeclassify);
 
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GlobalPane;
    private javax.swing.JPanel UtilisateurPane;
    private javax.swing.JPanel centerPane;
    private javax.swing.JTextField dirName;
    private javax.swing.JTextArea fileContainer;
    private javax.swing.JPanel headerPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextAreaContent;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JPanel statusPane;
    private javax.swing.JTextPane statusTxt;
    private javax.swing.JPanel titlePane;
    private javax.swing.JButton training;
    private javax.swing.JTextPane txtHeader;
    // End of variables declaration//GEN-END:variables

    /** Creates new form Securetransfert */
    /************************************************************************************/
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ClassifyInABox().setVisible(true);
            }
        });
    }

    public ClassifyInABox() {
        initComponents();

        fileContainer.setDropTarget(new DropTarget(fileContainer, new FileDropTargetListener(fileContainer, statusTxt)));
    }
}
