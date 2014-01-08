/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PropertyListener implements PropertyChangeListener {
    
    private MainWindow mainWindow;
    
    public PropertyListener(MainWindow memWindow) {
        this.mainWindow = memWindow;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("AuthReceived")) {
            mainWindow.authReceieved(evt.getNewValue().toString());
            System.out.println(evt.getNewValue().toString());
        }
    }
    
}
