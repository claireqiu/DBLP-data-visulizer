import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * Created by clair on 11/29/2016.
 */
public class AuthorGUI extends JDialog{
    private JPanel contentPanel;
    private JPanel westPanel;
    private JPanel eastPanel;
    private JTextField nameField;
    private JLabel nameLabel;
    private JLabel numOfPubLabel;
    private JTextField numOfPubField;
    private JLabel avgnumOfPubLabel;
    private JTextField avgnumOfPubField;
    private JLabel numOfSourcesLabel;
    private JTextField numOfSourcesField;
    private JLabel startYearLabel;
    private JTextField startYearField;
    private JLabel endYearLabel;
    private JTextField endYearField;

    public AuthorGUI(){
        setContentPane(contentPanel);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        dispose();
    }

    public void setFields(HashMap<String,String> authorInfo ){
        nameField.setText(authorInfo.get("name"));
        numOfPubField.setText(authorInfo.get("numOfPub"));
        avgnumOfPubField.setText(authorInfo.get("avgnumOfPub"));
        numOfSourcesField.setText(authorInfo.get("numOfSources"));
        startYearField.setText(authorInfo.get("startYear"));
        endYearField.setText(authorInfo.get("endYear"));
    }
}
