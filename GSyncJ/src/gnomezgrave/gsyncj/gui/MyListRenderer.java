/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author praneeth
 */
public class MyListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        //JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof JLabel) {
            this.setText(((JLabel) value).getText());
            this.setIcon(((JLabel) value).getIcon());
            this.setBackground(isSelected ? Color.GRAY : Color.white);
            //this.setForeground(isSelected ? Color.white : Color.black);
        }
        return this;
    }
}
