package com.gitlab.uu.vinproffsen.ui;

import com.gitlab.uu.mvp.View;
import com.gitlab.uu.vinproffsen.items.Wine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wine dialog that opens when double clicking a wine in the table.
 *
 * @author Niklas Persson
 * @version 2016-03-17
 */
public class WineDialog extends JDialog {
    private final static Logger LOG = Logger.getLogger(WineDialog.class.getName());
    
    public WineDialog(Wine wine, View view) {
        if (wine == null) {
            dispose();
        } else {
            setTitle(wine.getFullName().toString());
            
            LOG.log(Level.FINE, "Opening wine dialog for wine: {0}", wine.toString());

            setLayout(new BorderLayout());

            JLabel wineInfo = new JLabel(wine.toString());
            wineInfo.setFont(wineInfo.getFont().deriveFont(Font.PLAIN));

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.setBorder(new EmptyBorder(3, 3, 3, 3));

            panel.add(createLabel("Pant", wine.getDeposit() / 100 + ":-"));
            panel.add(createLabel("Pris/liter: ", wine.getPricePerLiter() / 100 + ":-"));
            panel.add(createLabel("Sortiment: ", wine.getAssortment()));
            panel.add(createLabel("Råvaror: ", wine.getDescription()));
            panel.add(createLabel("Producent: ", wine.getProducer()));
            panel.add(createLabel("Leverantör: ", wine.getSupplier()));
            panel.add(createLabel("Förpackning: ", wine.getPackaging()));
            panel.add(createLabel("Förslutning: ", wine.getSeal()));

            JButton addWine = new JButton(new AbstractAction("Lägg till i vinlistan") {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    view.send("wine:list:add", Arrays.asList(wine));
                }
            });

            panel.add(addWine);

            add(wineInfo, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);

            setModalityType(ModalityType.DOCUMENT_MODAL);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    private JLabel createLabel(String key, String value) {
        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        label.setText("<html><b>" + key + ":</b> " + value + "</html>");

        return label;
    }
}
