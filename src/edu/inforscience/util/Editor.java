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
    private int textAlignment;

    public Editor()
    {
        this("", StyleConstants.ALIGN_LEFT);
    }

    public Editor(String content, int textAlignment)
    {
        textPane = new JTextPane();
        textPane.setText(content);
        setTextAlignment(textAlignment);
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
    }

    public String getText()
    {
        return textPane.getText();
    }


    private ImageIcon getImage(String name)
    {
        return new ImageIcon(getClass().getResource("/" + name + ".png"));
    }

    private void setTextAlignment(int newAlignment)
    {
        String text = textPane.getText();
        StyledDocument document = new DefaultStyledDocument();
        Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(defaultStyle, newAlignment);
        textPane.setDocument(document);
        textPane.setText(text);
        textAlignment = newAlignment;
    }

    public int getTextAlignment()
    {
        return textAlignment;
    }

    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton) e.getSource();
            if (source == alignLeftButton) {
                setTextAlignment(StyleConstants.ALIGN_LEFT);
            } else if (source == alignCenterButton) {
                setTextAlignment(StyleConstants.ALIGN_CENTER);
            } else if (source == alignRightButton) {
                setTextAlignment(StyleConstants.ALIGN_RIGHT);
            }
        }

    }
}
