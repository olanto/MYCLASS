/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

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
import java.net.*;
import javax.net.*;
import static org.olanto.util.Messages.*;


public class FileDropTargetListener implements  DropTargetListener {
    private JTextArea textArea;
    private JTextPane status;
    
    public FileDropTargetListener(JTextArea ta, JTextPane tp)   {
        textArea = ta;
        status = tp;
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);       
    }
    
    public void dragEnter(DropTargetDragEvent event)  {}
    public void dragExit(DropTargetEvent event)   {}
    public void dropActionChanged(DropTargetDragEvent event) { }
    
    public void dragOver(DropTargetDragEvent event)   {
        // provide visual feedback
        event.acceptDrag(DnDConstants.ACTION_COPY);
    }
    
    
    public synchronized void drop(DropTargetDropEvent event)  {
        
                if ((event.getSourceActions() & DnDConstants.ACTION_COPY) != 0){
            event.acceptDrop(DnDConstants.ACTION_COPY);         
        }
        else {
            event.rejectDrop();          
            return;
        }

        
        Transferable transferable = event.getTransferable();
        DataFlavor[] flavors = event.getCurrentDataFlavors();
        textArea.setText("");
        for (int i = 0; i < flavors.length; i++)   {
           
            DataFlavor dataFlavor = flavors[i];
            try  {
                if (dataFlavor.equals(DataFlavor.javaFileListFlavor)) {                 
                    java.util.List fileList = (java.util.List) transferable.getTransferData(dataFlavor);
                    File from = (File) fileList.get(0);
                    String fileName = from.getPath();
                    textArea.append("classify from file:\n");
                    textArea.append(fileName);
                    status.setText("\""+fileName+"\"  last classify");
                    String toBeclassify=readFromFile(fileName);
                    
                    textArea.append(Classify.advise(toBeclassify));
                     textArea.append("\n\ncontent from file:\n");
                    textArea.append(toBeclassify);
                    return;
                } else if (dataFlavor.equals(DataFlavor.stringFlavor)) {
                    msg(dataFlavor.toString());
                    String dropped = (String) transferable.getTransferData(dataFlavor);
                    textArea.append("String flavor: "+dropped);
                    textArea.append(Classify.advise(dropped));
                }
            } catch(Exception e)  {
                error("drop",e);
                
                event.dropComplete(false);
                
                return;
            }
        }
        event.dropComplete(false);
    }
    
        private static String readFromFile(String fname){
        StringBuffer b=new StringBuffer("");
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String w=in.readLine();
            while (w!=null){
                //System.out.println(w);
                b.append(w);
                w=in.readLine();
            }
        } catch (Exception e) {error("IO error in readFromFile",e);}
        return b.toString();
    }

}
