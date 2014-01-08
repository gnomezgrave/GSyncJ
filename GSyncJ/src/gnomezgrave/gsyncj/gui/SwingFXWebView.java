package gnomezgrave.gsyncj.gui;

import com.sun.javafx.application.PlatformImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SwingFXWebView
 */
public class SwingFXWebView extends JPanel {
    
    private Stage stage;
    private WebView browser;
    private JFXPanel jfxPanel;
    private WebEngine webEngine;
    private String url;
    
    private WebBrowser wb;
    private MainWindow mw;
    private Property property;
    
    public SwingFXWebView(MainWindow mw, WebBrowser wb, String url) {
        this.mw = mw;
        property = new Property();
        property.addPropertyChangeListener(new PropertyListener(mw));
        jfxPanel = new JFXPanel();
        
        this.wb = wb;
        
        setLayout(new BorderLayout());
        add(jfxPanel, BorderLayout.CENTER);
        this.url = url;
        setMinimumSize(new Dimension(800, 600));
    }
    
    public void reload() {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                webEngine.reload();
            }
        });
    }
    
    public void reloadURL() {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                webEngine.load(url);
            }
        });
    }

    /**
     * createScene
     *
     * Note: Key is that Scene needs to be created and run on "FX user thread"
     * NOT on the AWT-EventQueue Thread
     *
     */
    public void createScene() {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                
                stage = new Stage();
                
                stage.setTitle("Google Authentication");
                stage.setResizable(true);
                
                Group root = new Group();
                Scene scene = new Scene(root, 80, 20);
                stage.setScene(scene);

                // Set up the embedded browser:
                browser = new WebView();
                
                webEngine = browser.getEngine();
                
                webEngine.getLoadWorker().stateProperty().addListener(
                        new ChangeListener<State>() {
                            public void changed(ObservableValue ov, State oldState, State newState) {
                                try {
                                    if (newState == State.SUCCEEDED) {
                                        if (webEngine.getLocation().contains("accounts.google.com/o/oauth2/approval?as")) {
                                            Document document = webEngine.getDocument();
                                            Element elementById = document.getElementById("code");
                                            String code = elementById.getAttribute("value");
                                            if (wb.isIsNewProfile()) {
                                                wb.profileAdded(code);
                                            } else {
                                                property.propertyChanged("AuthReceived", null, code);
                                            }
                                            wb.setProcessed(true);
                                            wb.setVisible(false);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                
                            }
                        });
                
                webEngine.load(url);
                ObservableList<Node> children = root.getChildren();
                children.add(browser);
                
                jfxPanel.setScene(scene);
            }
        });
    }
}
