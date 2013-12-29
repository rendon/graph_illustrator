package edu.inforscience.util;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Editor extends JPanel {
    private JTextPane textPane;
    private JButton alignLeftButton;
    private JButton alignRightButton;
    private JButton alignCenterButton;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_RIGHT = 3;
    private int alignment;
    private String defaultText;

    public Editor()
    {
        this("");
    }

    public Editor(String content)
    {
        defaultText = content;
        textPane = new JTextPane();
        textPane.setText(defaultText);
        textPane.setPreferredSize(new Dimension(textPane.getWidth(), 100));
        JScrollPane scrollPane = new JScrollPane(textPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        alignLeftButton = new JButton(getImage("align_left"));
        alignCenterButton = new JButton(getImage("align_center"));
        alignRightButton = new JButton(getImage("align_right"));
        ActionHandler actionHandler = new ActionHandler();
        alignLeftButton.addActionListener(actionHandler);
        alignCenterButton.addActionListener(actionHandler);
        alignRightButton.addActionListener(actionHandler);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tools.add(alignLeftButton);
        tools.add(alignCenterButton);
        tools.add(alignRightButton);
        add(tools, BorderLayout.SOUTH);
        alignment = ALIGN_LEFT;
    }

    public void clear()
    {
        StyledDocument document = new DefaultStyledDocument();
        Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(defaultStyle, StyleConstants.ALIGN_LEFT);
        textPane.setDocument(document);
    }

    public String getText()
    {
        if (alignment == ALIGN_LEFT)
            return justifyLeft(textPane.getText());
        else if (alignment == ALIGN_CENTER)
            return justifyCenter(textPane.getText());
        else
            return justifyRight(textPane.getText());
    }

    private String justifyLeft(String text)
    {
        return text;
    }

    private String justifyCenter(String text)
    {
        String[] lines = text.split("\n");
        int n = lines.length;
        int maxLength = 0;
        for (int i = 0; i < n; i++) {
            lines[i] = lines[i].trim();
            maxLength = Math.max(maxLength, lines[i].length());
        }

        for (int i = 0; i < n; i++) {
            int spaces = (maxLength - lines[i].length()) / 2;
            String s = "";
            for (int j = 0; j < spaces; j++)
                s += " ";
            lines[i] = s + lines[i];

            //while (lines[i].length() < cols)
            //    lines[i] += " ";
        }

        String centered = "";
        for (int i = 0; i < n; i++) {
            centered += lines[i];
            if (i < n - 1)
                centered += "\n";
        }

        System.out.println("Result: " + centered);
        return centered;
    }

    private String justifyRight(String text)
    {
        return text;
    }

    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String text = textPane.getText();
            JButton source = (JButton) e.getSource();
            StyledDocument document = new DefaultStyledDocument();
            Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
            if (source == alignLeftButton) {
                StyleConstants.setAlignment(defaultStyle,
                        StyleConstants.ALIGN_LEFT);
                textPane.setDocument(document);
                textPane.setText(text);
                alignment = ALIGN_LEFT;
            } else if (source == alignCenterButton) {
                StyleConstants.setAlignment(defaultStyle,
                        StyleConstants.ALIGN_CENTER);
                textPane.setDocument(document);
                textPane.setText(text);
                alignment = ALIGN_CENTER;
            } else if (source == alignRightButton) {
                StyleConstants.setAlignment(defaultStyle,
                        StyleConstants.ALIGN_RIGHT);
                textPane.setDocument(document);
                textPane.setText(text);
                alignment = ALIGN_RIGHT;
            }
        }

    }
}
