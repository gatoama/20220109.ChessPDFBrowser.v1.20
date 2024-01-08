/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.desktop.view;

/**
* This is a Demo application created
*/
import com.frojasg1.general.desktop.view.editorkits.WrapEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.*;

public class RTFTest extends JApplet implements ActionListener
{

  public static void main(String args[])
  {
    JFrame frame = new JFrame("Test");
    frame.setSize(300, 200);
    frame.setLocation(50, 50);
    RTFTest test = new RTFTest();
    test.init();
    frame.getContentPane().add("Center", test);

    frame.show();
    frame.addWindowListener(new java.awt.event.WindowAdapter()
    {
      public void windowClosing(java.awt.event.WindowEvent event)
      {
        System.exit(0); // close the application
      }
    });
  }

  /**
   * constructor
   */
  public RTFTest()
  {
  }

  public void init()
  {
    super.init();

    getContentPane().setLayout(new BorderLayout());

    // xxxxx main part of UI
    JPanel common = new JPanel(new BorderLayout());

    JTextPane jtp = new JTextPane();
//    JEditorPane jtp = new JEditorPane( "text/rtf", "" );
//    String rtftext = "{\\rtf1\\ansi{\\fonttbl\\f0\\fnil Monospaced;\\f1\\fnil\\u26032 ?\\u32048 ?\\u26126 ?\\u39636 ?;\\f2\\fnil\\u65325?\\u65331 ? \\u12468 ?\\u12471 ?\\u12483 ?\\u12463 ?;}\\ql\\f1\\fs60\\cf0 Test\\f2 \\par\\ql\\f2\\fs60\\cf0 \\par}";
    String rtftext = "{\\rtf1\\ansi\n" +
"{\\fonttbl\\f0\\fnil Monospaced;\\f1\\fnil Dialog;\\f2\\fnil\\u26032 ?\\u32048 ?\\u26126 ?\\u39636 ?;\\f3\\fnil\\u65325 ?\\u65331 ? \\u12468 ?\\u12471 ?\\u12483 ?\\u12463 ?;}\n" +
"{\\colortbl\\red0\\green0\\blue0;\\red51\\green51\\blue51;}\n" +
"\n" +
"\\li0\\ri0\\fi0\\ql\\f2\\fs60\\i0\\b0\\ul0\\cf0\\u1069 ?\\u1090 ?\\u1086 ? \\u1087 ?\\u1088 ?\\u1080 ?\\u1083 ?\\u1086 ?\\u1078 ?\\u1077 ?\\u1085 ?\\u1080 ?\\u1077 ? \\f3\\par\n" +
"\\par\n" +
"\\li0\\ri0\\fi0\\f1\\fs24\\ul0\\cf1\\par\n" +
"}";


//	char[] buf = rtftext.toCharArray();
    std = new DefaultStyledDocument();

    RTFEditorKit kit = new RTFEditorKit();
//		jtp.setEditorKit( new WrapEditorKit() );
    try
    {
		BufferedReader br = new BufferedReader(new FileReader( "J:\\RTF2.rtf" ));

//		CharArrayReader read = new CharArrayReader(buf);
       kit.read(br, std, 0);
       jtp.setStyledDocument(std);
//       jtp.setDocument(std);
       br.close();
    }
    catch (Exception streamException)
    {
        System.out.println("Failed to Read from file: " +
                                               streamException.toString());
        streamException.printStackTrace();
    }

    common.add("Center", jtp);

    getContentPane().add("Center", common);

    // xxxxx buttons of UI
    _buttonPanel = new JPanel();
    _buttonPanel.setLayout(new GridLayout(0, 1));
    common.add("East", _buttonPanel);

    _buttonToRTF = new JButton("Save RTF");
    _buttonPanel.add(_buttonToRTF);

    _buttonToRTF.addActionListener(this);

  }

  public void actionPerformed(ActionEvent e)
  {

    if (e.getSource() == _buttonToRTF)
    {
      StyledDocument doc = std;
      int length = doc.getLength();
      RTFEditorKit kit = new RTFEditorKit();

      try
      {
        // Write the Document's content into the RTF file
        FileOutputStream outStream = new FileOutputStream("J:\\RTF2.rtf");
        kit.write(outStream,std,0,length);
        outStream.close();

        ////////////////Print out the Document's content i.e. RTF ///////////
        PipedOutputStream pos = new PipedOutputStream();

        PipedInputStream pis = new PipedInputStream(pos);

        InputStreamReader isr = new InputStreamReader(pis, "UTF8");

        kit.write(pos, doc, 0, length);
        pos.flush();
        pos.close();

        StringBuffer buffer = new StringBuffer();
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1)
        {
           buffer.append((char)ch);
        }
        in.close();
        System.out.println("RTF O/P::"+buffer.toString());
        ///////////////////////////////////////////////////////////////
      }
      catch (Exception streamException)
      {
        System.out.println("Failed to write to file: " +
                                                  streamException.toString());
        streamException.printStackTrace();
      }
    }
  }


  private JPanel _buttonPanel = null;
  private JButton _buttonToRTF = null;
  private StyledDocument std = null;

}
