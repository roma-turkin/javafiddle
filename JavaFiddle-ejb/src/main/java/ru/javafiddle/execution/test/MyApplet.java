package ru.javafiddle.execution.test;

import ru.javafiddle.core.ejb.CompileAndRunBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by nk on 17.03.2016.
 */


public class MyApplet extends JApplet {

    private final JTextArea errors = new JTextArea();
    private final JTextArea errorsSec = new JTextArea();
    private final PlotPanel plotPanel = new PlotPanel();
    private final PlotPanel plotPanelSec = new PlotPanel();
    public MyApplet() {
        Container c = this;
        JScrollPane scrollPane = new JScrollPane();
        JScrollPane scrollPaneSec = new JScrollPane();
        scrollPane.setViewportView(errors);
        scrollPaneSec.setViewportView(errorsSec);
        add(plotPanel);
        add(plotPanelSec);
        c.add(scrollPane);
        c.add(scrollPaneSec);

    }
    public void paint (Graphics g) {
        super.paint(g);
        g.drawString("Hello", 25, 25);
    }

    JButton jb = new JButton("Button 1");
    JButton jb2 = new JButton("Button 2");

    public void init() {
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompileAndRunBean compileAndRunBean = new CompileAndRunBean();
                errors.setText(compileAndRunBean.compile("BadHello"));
                errorsSec.setText(compileAndRunBean.run("BadHello"));
                jb.setText("Button clicking");
            }
        });
        Container cp = getContentPane();
        cp.setLayout(new FlowLayout());
        cp.add(jb);

        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompileAndRunBean compileAndRunBean = new CompileAndRunBean();
                errors.setText(compileAndRunBean.compile("123"));
                errorsSec.setText(compileAndRunBean.run("123"));
                jb2.setText("Button clicking");
            }
        });
        cp.setLayout(new FlowLayout());
        cp.add(jb2);
    }


    static class PlotPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        BufferedImage image;

        @Override
        public void paint(final Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            } else {
                g.setColor(Color.lightGray);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        new MyApplet();
    }
}
